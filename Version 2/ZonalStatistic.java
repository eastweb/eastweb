package edu.sdstate.eastweb.prototype;

public class ZonalStatistic {
    private double mCount;
    private double mSum;
    private double mMax;
    private double mMin;
    private double mMean;
    private double mStandardDeviation;

    public ZonalStatistic(double count, double sum, double max, double min,
            double mean, double standardDeviation) {
        mCount = count;
        mSum = sum;
        mMax = max;
        mMin = min;
        mMean = mean;
        mStandardDeviation = standardDeviation;
    }

    double getCount() {
        return mCount;
    }

    double getSum() {
        return mSum;
    }

    double getMax() {
        return mMax;
    }

    double getMin() {
        return mMin;
    }

    double getMean() {
        return mMean;
    }

    double getStandardDeviation() {
        return mStandardDeviation;
    }
}
