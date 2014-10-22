package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;

/**
 * uses the same logic for ndwi5 and ndwi6
 * NDWI5 = (NIR-SWIR2)/(NIR+SWIR2)
 * NDWI6 = (NIR-SWIR)/(NIR+SWIR)
 * @author Isaiah Snell-Feikema
 */
public class GdalNDWI5Calculator extends IndicesFramework {

    private static final int NIR = 0;
    private static final int SWIR = 1;

    public GdalNDWI5Calculator(ProjectInfo mProject, DataDate mDate, String feature, EnvironmentalIndex mIndex ) throws ConfigReadException {
        File[] inputs = new File[2];
        inputs[NIR] =  DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.NBAR, feature, "Nadir_Reflectance_Band2");
        inputs[SWIR] = DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.NBAR, feature, "Nadir_Reflectance_Band5");

        setInputFiles(inputs);
        setOutputFile(DirectoryLayout.getIndex(mProject, mIndex, mDate, feature));
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
