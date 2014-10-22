package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;
import org.gdal.gdal.*;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class TiffFixer {
    /**
     * Resaves the specified tiff using GDAL so that ArcGIS 10 recognizes
     * the central meridian correctly. Input tiff and output MUST have
     * different names or it will mess up the input tiff.
     * 
     * @param input
     * @param output
     */
    public static void fixTiff(File input, File output) {
        synchronized (GdalUtils.lockObject) {
            GdalUtils.register();

            Dataset inputDS = gdal.Open(input.toString());
            Driver driver = gdal.GetDriverByName("GTiff");
            Dataset outputDS = driver.CreateCopy(output.toString(), inputDS);
            outputDS.delete();
            inputDS.delete();
        }
    }

}