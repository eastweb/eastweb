package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;

public class GdalLST_MEANCalculator extends IndicesFramework {

    final int DAY_LST = 0;
    final int NIGHT_LST = 1;
    final double mMin;
    final double mMax;

    public GdalLST_MEANCalculator(ProjectInfo mProject, DataDate mDate, String feature, EnvironmentalIndex mIndex ) throws ConfigReadException {
        mMin = mProject.getMinLst() + 273.15;
        mMax = mProject.getMaxLst() + 273.15;

        File[] inputFiles = new File[2];
        inputFiles[DAY_LST] = DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.LST, feature, "LST_Day_1km");
        inputFiles[NIGHT_LST] = DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.LST, feature, "LST_Night_1km");

        setInputFiles(inputFiles);
        setOutputFile(DirectoryLayout.getIndex(mProject, mIndex, mDate, feature));
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
