package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

public class GdalMeanLstCalculator extends GdalSimpleIndexCalculator {

    final int DAY_LST = 0;
    final int NIGHT_LST = 1;
    final double mMin;
    final double mMax;

    public GdalMeanLstCalculator(File day, File night, File mean, double min,
            double max) {
        mMin = min;
        mMax = max;

        File[] inputFiles = new File[2];
        inputFiles[DAY_LST] = day;
        inputFiles[NIGHT_LST] = night;

        setInputFiles(inputFiles);
        setOutputFile(mean);
    }

    @Override
    protected double calculatePixelValue(double[] values) {
        if (values[DAY_LST] == 32767 || values[NIGHT_LST] == 32767
                || values[DAY_LST] < mMin || values[DAY_LST] > mMax
                || values[NIGHT_LST] < mMin || values[NIGHT_LST] > mMax) {
            return -3.4028234663852886E38;
        } else {
            return (values[DAY_LST] + values[NIGHT_LST]) / 2;
        }
    }
}
