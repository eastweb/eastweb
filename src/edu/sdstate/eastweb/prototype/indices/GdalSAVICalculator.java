package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;

public class GdalSAVICalculator extends IndicesFramework {

    private final static double L = 0.5;
    private final static int RED = 0;
    private final static int NIR = 1;

    public GdalSAVICalculator(ProjectInfo mProject, DataDate mDate, String feature, EnvironmentalIndex mIndex ) throws ConfigReadException  {
        File[] inputs = new File[2];
        inputs[RED] = DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.NBAR, feature, "Nadir_Reflectance_Band1");
        inputs[NIR] = DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.NBAR, feature, "Nadir_Reflectance_Band2");

        setInputFiles(inputs);
        setOutputFile(DirectoryLayout.getIndex(mProject, mIndex, mDate, feature));
    }

    @Override
    protected double calculatePixelValue(double[] values) {
        if (values[NIR] == 32767 || values[RED] == 32767) {
            return -3.40282346639e+038;
        } else {
            return (values[NIR] - values[RED] * (1 + L))
                    / (values[NIR] + values[RED] + L);
        }
    }
}
