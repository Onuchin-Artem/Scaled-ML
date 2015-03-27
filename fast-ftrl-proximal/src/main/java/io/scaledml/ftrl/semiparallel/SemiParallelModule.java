package io.scaledml.ftrl.semiparallel;


import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.scaledml.ftrl.*;
import io.scaledml.ftrl.inputformats.InputFormat;
import io.scaledml.ftrl.inputformats.VowpalWabbitFormat;
import io.scaledml.ftrl.outputformats.CollectStatisticsOutputFormat;
import io.scaledml.ftrl.outputformats.NullOutputFormat;
import io.scaledml.ftrl.outputformats.OutputFormat;
import io.scaledml.ftrl.io.LineBytesBuffer;
import io.scaledml.ftrl.outputformats.PrintStreamOutputFormat;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executors;

public class SemiParallelModule extends AbstractModule {
    private final FtrlOptions options;

    public SemiParallelModule(FtrlOptions options) {
        this.options = options;
    }

    @Override
    protected void configure() {
        ThrowingProviderBinder.forModule(this);
        bindConstant().annotatedWith(Names.named("testOnly")).to(options.testOnly());
        bind(new TypeLiteral<EventHandler<SparseItem>>(){}).to(LearnEventHandler.class).asEagerSingleton();
        bind(FtrlProximalModelUpdater.class).to(LocalFtrlProximalModelUpdater.class).asEagerSingleton();
        bind(FtrlProximalRunner.class).asEagerSingleton();
        bind(InputFormat.class).to(VowpalWabbitFormat.class);
        bind(new TypeLiteral<WorkHandler<LineBytesBuffer>>() {}).to(ParseInputWorkHandler.class);
    }

    @Provides
    @Singleton
    FtrlProximalModel model() throws Exception {
        if (options.initialRegressor() != null) {
            return FtrlProximalModel.loadModel(Paths.get(options.initialRegressor()));
        }
        return new FtrlProximalModel()
                .alfa(options.alfa())
                .beta(options.beta())
                .lambda1(options.l1())
                .lambda2(options.l2())
                .featuresNumber(1L << options.hashcodeBits());
    }


    @Provides
    @Singleton
    OutputFormat outputFormat() throws IOException {
        CollectStatisticsOutputFormat format = new CollectStatisticsOutputFormat();
        if (options.predictions() == null) {
            format.delegate(new NullOutputFormat());
        } else if (options.predictions().equals("/dev/stdout")) {
            format.delegate(new PrintStreamOutputFormat().outputStream(System.out));
        } else {
            format.delegate(new PrintStreamOutputFormat()
                    .outputStream(new PrintStream(new FastBufferedOutputStream(
                            Files.newOutputStream(Paths.get(options.predictions()))))));
        }
        return format;
    }

    @Provides
    @Singleton
    InputStream inputStream() throws IOException {
        if (options.data() == null) {
            return System.in;
        }
        return Files.newInputStream(Paths.get(options.data()));
    }

    @Provides
    @Singleton
    @Named("inputDisruptor")
    Disruptor<LineBytesBuffer> inputDisruptor(Provider<WorkHandler<LineBytesBuffer>> handlerProvider) {
        Disruptor<LineBytesBuffer> disruptor = new Disruptor<>(
                new EventFactory<LineBytesBuffer>() {
                    @Override
                    public LineBytesBuffer newInstance() {
                        return new LineBytesBuffer();
                    }
                },
                2048, Executors.newCachedThreadPool(DaemonThreadFactory.INSTANCE),
                ProducerType.SINGLE, new SleepingWaitStrategy());
        WorkHandler<LineBytesBuffer>[] parsers = new WorkHandler[options.threads()];
        for (int i = 0; i < options.threads(); i++) {
            parsers[i] = handlerProvider.get();
        }
        disruptor.handleEventsWithWorkerPool(parsers);
        return disruptor;
    }

    @Provides
    @Singleton
    @Named("secondDisruptor")
    Disruptor<?> secondDisruptor(@Named("secondDisruptor") Disruptor<SparseItem> secondDisruptor) {
        return secondDisruptor;
    }

    @Provides
    @Singleton
    @Named("secondDisruptor")
    Disruptor<SparseItem> secondDisruptor(EventHandler<SparseItem> learnHandler) {
        Disruptor<SparseItem> disruptor = new Disruptor<>(
                new EventFactory<SparseItem>() {
                    @Override
                    public SparseItem newInstance() {
                        return new SparseItem();
                    }
                },
                2048, Executors.newCachedThreadPool(DaemonThreadFactory.INSTANCE),
                ProducerType.MULTI, new SleepingWaitStrategy());
        disruptor.handleEventsWith(learnHandler);
        return disruptor;
    }

    @Provides
    @Named("featuresNumber")
    public long featuresNumber(FtrlProximalModel model) {
        return model.featuresNumber();
    }

    @Provides
    @Singleton
    Optional<Path> outputForModelPath() {
        return options.finalRegressor() == null ? Optional.<Path>empty() : Optional.of(Paths.get(options.finalRegressor()));
    }
}