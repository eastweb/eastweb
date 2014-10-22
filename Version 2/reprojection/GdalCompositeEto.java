package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class GdalCompositeEto implements CompositeEto {

    @Override
    public void composite(List<File> inputs, File output) throws Exception {
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {
            List<Dataset> inputDSs = new ArrayList<Dataset>();
            for (File input : inputs) {
                inputDSs.add(gdal.Open(input.getPath()));
            }

            Dataset outputDS = gdal.GetDriverByName("GTiff").Create(
                    output.getPath(),
                    inputDSs.get(0).GetRasterXSize(), inputDSs.get(0).GetRasterYSize(),
                    1,
                    gdalconst.GDT_Float32
            );
            outputDS.SetGeoTransform(inputDSs.get(0).GetGeoTransform());
            outputDS.SetProjection(inputDSs.get(0).GetProjection());

            // FIXME: make use less ram?
            double[] inputArray = new double[inputDSs.get(0).GetRasterXSize() * inputDSs.get(0).GetRasterYSize()];
            double[] outputArray = new double[inputDSs.get(0).GetRasterXSize() * inputDSs.get(0).GetRasterYSize()];
            for (Dataset inputDS : inputDSs) {
                inputDS.GetRasterBand(1).ReadRaster(0, 0, inputDS.GetRasterXSize(), inputDS.GetRasterYSize(), inputArray);
                for (int i=0; i<inputArray.length; i++) {
                    outputArray[i] += inputArray[i];
                }
            }

            for (int i=0; i<inputArray.length; i++) {
                outputArray[i] /= inputDSs.size();
            }

            outputDS.GetRasterBand(1).WriteRaster(0, 0, inputDSs.get(0).GetRasterXSize(), inputDSs.get(0).GetRasterYSize(), outputArray);

            for (Dataset inputDS : inputDSs) {
                inputDS.delete();
            }
            outputDS.delete();
        }
    }

}
