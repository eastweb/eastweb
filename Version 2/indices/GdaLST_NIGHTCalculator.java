package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;

public class GdaLST_NIGHTCalculator extends IndicesFramework {

    final static int INPUT = 0;
    final double mMin;
    final double mMax;

    public GdaLST_NIGHTCalculator(ProjectInfo mProject, DataDate mDate, String feature, EnvironmentalIndex mIndex ) throws ConfigReadException {
        File[] inputFiles = new File[1];
        inputFiles[INPUT] = DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.LST, feature, "LST_Night_1km");

        setInputFiles(inputFiles);
        setOutputFile(DirectoryLayout.getIndex(mProject, mIndex, mDate, feature));

        mMin = mProject.getMinLst() + 273.15;
        mMax = mProject.getMaxLst() + 273.15;
    }

    @Override
    protected double calculatePixelValue(double[] values) {
        if (values[INPUT] < mMin || values[INPUT] > mMax) {
            return -3.4028234663852886E38;
        } else {
            return values[INPUT];
        }
    }
}
