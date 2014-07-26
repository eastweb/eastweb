package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

/**
 * uses the same logic for ndwi5 and ndwi6
 * NDWI5 = (NIR-SWIR2)/(NIR+SWIR2)
 * NDWI6 = (NIR-SWIR)/(NIR+SWIR)
 * @author Isaiah Snell-Feikema
 */
public class GdalNdwiCalculator extends GdalSimpleIndexCalculator {

    private static final int NIR = 0;
    private static final int SWIR = 1;

    public GdalNdwiCalculator(File nir, File swir, File output) {
        File[] inputs = new File[2];
        inputs[NIR] = nir;
        inputs[SWIR] = swir;

        setInputFiles(inputs);
        setOutputFile(output);
    }

    @Override
    protected double calculatePixelValue(double[] values) {
        if (values[NIR] == 32767 || values[SWIR] == 32767) {
            return -3.40282346639e+038;
        } else {
            return (values[NIR] - values[SWIR]) / (values[SWIR] + values[NIR]);
        }
    }
}
