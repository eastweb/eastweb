package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

public class GdalSaviCalculator extends GdalSimpleIndexCalculator {

    private final static double L = 0.5;
    private final static int RED = 0;
    private final static int NIR = 1;

    public GdalSaviCalculator(File red, File nir, File output) {
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
            return ((values[NIR] - values[RED]) * (1 + L))
                    / (values[NIR] + values[RED] + L);
        }
    }
}
