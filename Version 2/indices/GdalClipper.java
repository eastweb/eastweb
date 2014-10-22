package edu.sdstate.eastweb.prototype.indices;

import java.io.File;
import java.util.Arrays;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Transformer;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class GdalClipper implements Clipper {

    private final File mRaster;
    private final File mFeature;
    private final File mOutput;
    //private final String mFormat;

    /**
     * The feature extent must be a subset of the raster extent.
     * 
     * @param raster Filename of GDAL supported raster with one band.
     * @param feature Filename of GDAL supported feature.
     * @param output Filename for GeoTiff output raster.
     * @param format GDAL format string with copy creation support. @see [GDAL format str html]
     */
    public GdalClipper(File raster, File feature, File output, String format) {
        mRaster = raster;
        mFeature = feature;
        mOutput = output;
        //mFormat = format;
    }

    @Override
    public void clip() throws Exception {
        GdalUtils.register();

        synchronized (GdalUtils.lockObject) {
            Dataset rasterDS = gdal.Open(mRaster.getPath());
            DataSource featureDS = ogr.Open(mFeature.getPath());
            Layer featureLyr = featureDS.GetLayer(0);

            final int pixelSize = (int) Math.abs(rasterDS.GetGeoTransform()[1]); // FIXME: getting pixel size won't work for some datasets
            System.out.println("PIXEL SIZE: " + pixelSize);

            double[] featureExtent = featureLyr.GetExtent();
            System.out.println(Arrays.toString(featureExtent));

            Dataset outputDS = gdal.GetDriverByName("GTiff").Create(
                    mOutput.getPath(),
                    (int) Math.ceil((featureExtent[1]-featureExtent[0])/pixelSize),
                    (int) Math.ceil((featureExtent[3]-featureExtent[2])/pixelSize),
                    1,
                    gdalconst.GDT_Int16
                    );

            outputDS.SetProjection(featureLyr.GetSpatialRef().ExportToWkt());
            outputDS.SetGeoTransform(new double[] {
                    featureExtent[0], pixelSize, 0,
                    featureExtent[2] + outputDS.GetRasterYSize()*pixelSize, 0, -pixelSize
            });

            System.out.println(Arrays.toString(outputDS.GetGeoTransform()));

            // Get pixel coordinate in output raster of corner of zone raster
            Transformer transformer = new Transformer(outputDS, rasterDS, null);

            double[] point = new double[] {-0.5, -0.5, 0}; // Location of corner of first zone raster pixel

            transformer.TransformPoint(0, point);
            //int xOffset = (int) Math.round(point[0]);
            //int yOffset = (int) Math.round(point[1]);

            Dataset maskDS = gdal.GetDriverByName("MEM").Create(
                    "",
                    (int) Math.ceil((featureExtent[1]-featureExtent[0])/pixelSize),
                    (int) Math.ceil((featureExtent[3]-featureExtent[2])/pixelSize),
                    1,
                    gdalconst.GDT_Int16);

            maskDS.SetProjection(featureLyr.GetSpatialRef().ExportToWkt());
            //zoneDS.SetProjection(rasterDS.GetProjection());
            maskDS.SetGeoTransform(new double[] {
                    featureExtent[0], pixelSize, 0,
                    featureExtent[2] + outputDS.GetRasterYSize()*pixelSize, 0, -pixelSize
            });

            maskDS.GetRasterBand(1).Fill(0); // FIXME: necessary?

            gdal.RasterizeLayer(maskDS, new int[] {1}, featureLyr);

            int[] maskArray = new int[maskDS.GetRasterXSize()];
            int[] rasterArray = new int[maskDS.GetRasterXSize()];
            for (int y=0; y<maskDS.GetRasterYSize(); y++) {
                maskDS.GetRasterBand(1).ReadRaster(0, y, maskDS.GetRasterXSize(), 1, maskArray);
                /* removed offsets from the parameters in the following statement
                 *  rasterDS.GetRasterBand(1).ReadRaster(xOffset, yOffset + y, maskDS.GetRasterXSize(), 1, rasterArray);
                 *  8/28/13 by J. Hu
                 */

                rasterDS.GetRasterBand(1).ReadRaster(0, y, maskDS.GetRasterXSize(), 1, rasterArray);
                for (int i=0; i<maskArray.length; i++) {
                    if (maskArray[i] == 0) { // FIXME
                        rasterArray[i] = 32767; // FIXME
                    }
                }

                outputDS.GetRasterBand(1).WriteRaster(0, y, maskDS.GetRasterXSize(), 1, rasterArray);
            }

            // Calculate statistics
            for (int i=1; i<=outputDS.GetRasterCount(); i++) {
                Band band = outputDS.GetRasterBand(i);

                band.SetNoDataValue(32767); // FIXME
                band.ComputeStatistics(false);
            }

            maskDS.GetRasterBand(1).ComputeStatistics(false);

            maskDS.delete();
            rasterDS.delete();
            outputDS.delete();
        }
    }

}
