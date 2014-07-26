package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

/**
 * EVI = G * (NIR - RED)/(NIR + C1*RED - C2*BLUE + L) where L=1, C1=6, C2=7.5,
 * and G=2.5
 * 
 * @author Isaiah Snell-Feikema
 */
public class GdalEviCalculator extends GdalSimpleIndexCalculator {

    private static final double L = 1;
    private static final double C1 = 6;
    private static final double C2 = 7.5;
    private static final double G = 2.5;

    private static final int RED = 0;
    private static final int NIR = 1;
    private static final int BLUE = 2;

    public GdalEviCalculator(File red, File nir, File blue, File output) {
        File[] inputs = new File[3];
        inputs[RED] = red;
        inputs[NIR] = nir;
        inputs[BLUE] = blue;

        setInputFiles(inputs);
        setOutputFile(output);
    }

    @Override
    protected double calculatePixelValue(double[] values) {
        if (values[NIR] == 32767 || values[RED] == 32767
                || values[BLUE] == 32767) {
            return -3.40282346639e+038;
        } else {
            return G * (values[NIR] - values[RED])
                    / (values[NIR] + C1 * values[RED] - C2 * values[BLUE] + L);
        }
    }
}
