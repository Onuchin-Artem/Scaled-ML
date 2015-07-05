package io.scaledml.ftrl.options;


import io.scaledml.core.inputformats.InputFormat;

public class FtrlOptionsObject implements FtrlOptions {
    private int hashcodeBits = 18;
    private double alfa = 0.005;
    private double beta = 0.1;
    private double l1 = 0.;
    private double l2 = 0.;
    private String predictions;
    private String data;
    private String finalRegressor;
    private String initialRegressor;
    private boolean testOnly = false;
    private int threads = 1;
    private boolean parallelLearn = false;
    private boolean quadratic = false;
    private InputFormatType format = InputFormatType.vw;
    private boolean skipFirst = false;
    private String csvMask = "lc";
    private char csvDelimiter = ',';
    private int ringSize = 2048;
    private boolean featureEngineering;
    private String customInputFormatClass;

    @Override
    public boolean featureEngineering() {
        return featureEngineering;
    }

    @Override
    public int hashcodeBits() {
        return hashcodeBits;
    }

    @Override
    public double alfa() {
        return alfa;
    }

    @Override
    public double beta() {
        return beta;
    }

    @Override
    public double l1() {
        return l1;
    }

    @Override
    public double l2() {
        return l2;
    }

    @Override
    public String finalRegressor() {
        return finalRegressor;
    }

    @Override
    public String initialRegressor() {
        return initialRegressor;
    }

    @Override
    public boolean testOnly() {
        return testOnly;
    }

    @Override
    public String data() {
        return data;
    }

    @Override
    public String predictions() {
        return predictions;
    }

    @Override
    public boolean help() {
        return false;
    }

    @Override
    public int threads() {
        return threads;
    }

    @Override
    public boolean parallelLearn() {
        return parallelLearn;
    }

    @Override
    public InputFormatType format() {
        return format;
    }

    @Override
    public String customInputFormatClass() {
        return customInputFormatClass;
    }


    public FtrlOptionsObject customInputFormatClass(String customInputFormatClass) {
        this.customInputFormatClass = customInputFormatClass;
        return this;
    }

    public FtrlOptionsObject hashcodeBits(int hashcodeBits) {
        this.hashcodeBits = hashcodeBits;
        return this;
    }

    public FtrlOptionsObject format(InputFormatType format) {
        this.format = format;
        return this;
    }

    public FtrlOptionsObject alfa(double alfa) {
        this.alfa = alfa;
        return this;
    }

    public FtrlOptionsObject beta(double beta) {
        this.beta = beta;
        return this;
    }

    public FtrlOptionsObject skipFirst(boolean skipFirst) {
        this.skipFirst = skipFirst;
        return this;
    }

    public FtrlOptionsObject l1(double l1) {
        this.l1 = l1;
        return this;
    }

    public FtrlOptionsObject l2(double l2) {
        this.l2 = l2;
        return this;
    }

    public FtrlOptionsObject finalRegressor(String finalRegressor) {
        this.finalRegressor = finalRegressor;
        return this;
    }

    public FtrlOptionsObject initialRegressor(String initialRegressor) {
        this.initialRegressor = initialRegressor;
        return this;
    }

    public FtrlOptionsObject testOnly(boolean testOnly) {
        this.testOnly = testOnly;
        return this;
    }

    public FtrlOptionsObject data(String data) {
        this.data = data;
        return this;
    }

    public FtrlOptionsObject predictions(String predictions) {
        this.predictions = predictions;
        return this;
    }

    public FtrlOptionsObject threads(int threads) {
        this.threads = threads;
        return this;
    }

    public FtrlOptionsObject scalable(boolean scalable) {
        this.parallelLearn = scalable;
        return this;
    }

    public FtrlOptionsObject csvMask(String csvMask) {
        this.csvMask = csvMask;
        return this;
    }

    public FtrlOptionsObject featureEngineering(boolean featureEngineering) {
        this.featureEngineering = featureEngineering;
        return this;
    }
    @Override
    public int ringSize() {
        return ringSize;
    }

    public FtrlOptionsObject quadratic(boolean quadratic) {
        this.quadratic = quadratic;
        return this;
    }

    @Override
    public boolean skipFirst() {
        return skipFirst;
    }

    @Override
    public String csvMask() {
        return csvMask;
    }


    @Override
    public char csvDelimiter() {
        return csvDelimiter;
    }

    public FtrlOptionsObject csvDelimiter(char csvDelimiter) {
        this.csvDelimiter = csvDelimiter;
        return this;
    }

    public FtrlOptionsObject ringSize(int ringSize) {
        this.ringSize = ringSize;
        return this;
    }
}
