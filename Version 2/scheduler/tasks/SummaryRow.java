package edu.sdstate.eastweb.prototype.scheduler.tasks;

public class SummaryRow {
    private final String mFieldValue;
    private final double mCount;
    private final double mSum;
    private final double mMean;
    private final double mStdev;

    public SummaryRow(String fieldValue, double count, double sum, double mean, double stdev) {
        mFieldValue = fieldValue;
        mCount = count;
        mSum = sum;
        mMean = mean;
        mStdev = stdev;
    }

    public String getFieldValue() {
        return mFieldValue;
    }

    public double getCount() {
        return mCount;
    }

    public double getSum() {
        return mSum;
    }

    public double getMean() {
        return mMean;
    }

    public double getStdev() {
        return mStdev;
    }
}
