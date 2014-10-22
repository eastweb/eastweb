package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;

public class GdalNDVICalculator extends IndicesFramework {

    private static final int RED = 0;
    private static final int NIR = 1;

    public GdalNDVICalculator(ProjectInfo mProject, DataDate mDate, String feature, EnvironmentalIndex mIndex ) throws ConfigReadException {
        File[] inputFiles = new File[2];
        inputFiles[RED] = DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.NBAR, feature, "Nadir_Reflectance_Band1");
        inputFiles[NIR] =  DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.NBAR, feature, "Nadir_Reflectance_Band2");

        setInputFiles(inputFiles);
        setOutputFile(DirectoryLayout.getIndex(mProject, mIndex, mDate, feature));
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
