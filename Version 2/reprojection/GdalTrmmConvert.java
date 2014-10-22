package edu.sdstate.eastweb.prototype.reprojection;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.util.GdalUtils;


/**
 * FIXME: read metadata file provided for .bin file?
 * 
 * @author Isaiah Snell-Feikema
 *
 */
public class GdalTrmmConvert implements TrmmConverter {

    final TrmmProduct mProduct;
    final File mInput;
    final File mOutput;

    public static void main(String[] args) throws Exception {
        new GdalTrmmConvert(TrmmProduct.TRMM_3B42RT, new File(args[0]), new File(args[1])).convert();
    }

    public GdalTrmmConvert(TrmmProduct product, File input, File output) {
        mProduct = product;
        mInput = input;
        mOutput = output;
    }

    @Override
    public void convert() throws Exception {
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {
            // FIXME: not a very OO way to do things
            int ySize;
            int xSize = 1440;
            if (mProduct == TrmmProduct.TRMM_3B42) {
                ySize = 400;
            } else {
                ySize = 480;
            }

            Dataset outputDS = gdal.GetDriverByName("GTiff").Create(
                    mOutput.getPath(),
                    xSize, ySize,
                    1,
                    gdalconst.GDT_Float32
            );

            DataInputStream dis = new DataInputStream(new FileInputStream(mInput));

            double[] array = new double[1440];
            for (int row=0; row<ySize; row++) {
                for (int col=0; col<xSize; col++) {
                    array[col] = dis.readFloat();
                }

                outputDS.GetRasterBand(1).WriteRaster(0, row, xSize, 1, array);
            }
            dis.close();

            outputDS.GetRasterBand(1).SetNoDataValue(-99999.0);
            if (mProduct == TrmmProduct.TRMM_3B42) {
                outputDS.SetGeoTransform(new double[] {
                        0.125, 0.25, 0,
                        -49.8750000, 0, 0.25
                });
            } else {
                outputDS.SetGeoTransform(new double[] {
                        0.125, 0.25, 0,
                        -59.8750000, 0, 0.25
                });
            }

            outputDS.SetProjection("GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]");

            outputDS.GetRasterBand(1).ComputeStatistics(false);
            outputDS.delete();
        }
    }

}
