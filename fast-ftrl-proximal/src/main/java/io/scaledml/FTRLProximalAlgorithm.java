package io.scaledml;


import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.longs.*;

import java.io.Serializable;

public class FTRLProximalAlgorithm implements Serializable {

    private FtrlProximalState state;
    private double lambda1;
    private double lambda2;
    private double alfa;
    private double beta;
    private transient Long2DoubleMap notZeroN;
    private transient Long2DoubleMap notZeroZ;
    private transient Long2DoubleMap notZeroWeights;

    public FTRLProximalAlgorithm(long b, double lambda1, double lambda2, double alfa, double beta) {
        this();
        assert b < 64;
        long size = 1L << b;
        state = new LocalFtrlProximalState(size);
        this.alfa = alfa;
        this.beta = beta;
        this.lambda1 = lambda1;
        this.lambda2 = lambda2;
    }

    public FTRLProximalAlgorithm() {
    }

    private void tryInitTransientFields(int featuresNum) {
        if (notZeroN == null) {
            notZeroN = createMap(featuresNum);
            notZeroZ = createMap(featuresNum);
            notZeroWeights = createMap(featuresNum);
            this.state.initTransientFields(featuresNum);
        }
    }

    public static Long2DoubleMap createMap(int size) {
        return new Long2DoubleOpenHashMap(size * 2, Hash.FAST_LOAD_FACTOR);
    }

    public double train(SparseItem item) {
        tryInitTransientFields(item.getIndexes().size());
        calculateWeights(item);
        double predict = predict();
        double gradient = item.getLabel() - predict;

        FtrlProximalState.Increment increment = state.getIncrement();
        for (long index : item.getIndexes()) {
            double n = notZeroN.get(index);
            if (notZeroWeights.containsKey(index)) {
                double learning_rate = 1. / alfa * (Math.sqrt(n + gradient * gradient) -
                        Math.sqrt(n));
                increment.incrementZ(index, gradient - notZeroWeights.get(index) * learning_rate);
            } else {
                increment.incrementZ(index, gradient);
            }
            increment.incrementN(index, gradient * gradient);
        }
        state.writeIncrement();
        return predict;
    }

    public double test(SparseItem item) {
        tryInitTransientFields(item.getIndexes().size());
        calculateWeights(item);
        return predict();
    }

    private double predict() {
        return 1. / (1. + Math.exp(sum(notZeroWeights.values())));
    }

    private double sum(DoubleCollection values) {
        double sum = 0.;
        for (double e : values) {
            sum += e;
        }
        return sum;
    }

    private void calculateWeights(SparseItem item) {
        state.readVectors(item.getIndexes(), notZeroN, notZeroZ);
        notZeroWeights.clear();
        for (long index : item.getIndexes()) {
            double z = notZeroZ.get(index);
            double n = notZeroN.get(index);
            if (Math.abs(z) > lambda1) {
                notZeroWeights.put(index, -1. / ((beta + Math.sqrt(n)) / alfa + lambda2) * (z -
                        Math.signum(z) * lambda1));
            }
        }
    }

    public long featuresNum() {
        return state.size();
    }
}
