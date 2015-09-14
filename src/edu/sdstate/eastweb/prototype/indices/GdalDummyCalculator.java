package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

// FIXME: delete this. temporary (and dreadful) hack so I can release the
// software today
// Simply converts the input to 32 bits and saves it to the output location
public class GdalDummyCalculator extends GdalSimpleIndexCalculator {

    private final int INPUT = 0;

    public GdalDummyCalculator(File input, File output) {
        setInputFiles(new File[] { input });
        setOutputFile(output);
    }

    @Override
    protected double calculatePixelValue(double[] values) throws Exception {
        if (values[INPUT] == 32767) {
            return -3.4028234663852886E38;
        } else {
            return values[INPUT];
        }
    }

}
