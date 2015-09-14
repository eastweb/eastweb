package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

public class GdalNdviCalculator extends GdalSimpleIndexCalculator {

    private static final int RED = 0;
    private static final int NIR = 1;

    public GdalNdviCalculator(File red, File nir, File output) {
        File[] inputFiles = new File[2];
        inputFiles[RED] = red;
        inputFiles[NIR] = nir;

        setInputFiles(inputFiles);
        setOutputFile(output);
    }

    @Override
    protected double calculatePixelValue(double[] values) {
        if (values[NIR] == 32767 || values[RED] == 32767) {
            return -3.40282346639e+038;
        } else {
            return (values[NIR] - values[RED]) / (values[RED] + values[NIR]);
        }
    }

}
