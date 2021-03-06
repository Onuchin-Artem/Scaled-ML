package io.scaledml.ftrl.semiparallel;


import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import io.scaledml.core.SparseItem;
import io.scaledml.ftrl.AbstractParallelModule;
import io.scaledml.core.TwoPhaseEvent;
import io.scaledml.ftrl.options.FtrlOptions;
import io.scaledml.core.outputformats.OutputFormat;
import io.scaledml.ftrl.outputformats.StatisticsCalculator;

public class SemiParallelModule extends AbstractParallelModule<SparseItem> {

    public SemiParallelModule(FtrlOptions options) {
        super(options);
    }

    @Override
    protected void configure() {
        configureCommonBeans();
        bindConstant().annotatedWith(Names.named("statsCollectors")).to(1);
        bind(new TypeLiteral<EventHandler<TwoPhaseEvent<SparseItem>>>() {
        }).to(LearnEventHandler.class).asEagerSingleton();
        bind(new TypeLiteral<WorkHandler<TwoPhaseEvent<SparseItem>>>() {
        }).to(ParseInputWorkHandler.class);
        bind(OutputFormat.class).to(StatisticsCalculator.class).asEagerSingleton();
    }

    @Override
    protected EventFactory<SparseItem> outputEventFactory() {
        return SparseItem::new;
    }
}
