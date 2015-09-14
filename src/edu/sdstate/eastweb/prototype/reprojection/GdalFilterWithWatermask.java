package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.Transformer;
import org.gdal.gdal.gdal;

import edu.sdstate.eastweb.prototype.util.GdalUtils;

/**
 * 
 * 
 * @author Isaiah Snell-Feikema
 */
public class GdalFilterWithWatermask implements FilterWithWatermask {

    private File mInput;
    private File mWatermask;
    private File mOutput;

    public GdalFilterWithWatermask(File input, File watermask, File output) {
        mInput = input;
        mWatermask = watermask;
        mOutput = output;


        synchronized (GdalUtils.lockObject) {

        }
    }

    @Override
    public void filter() throws IOException {
        synchronized (GdalUtils.lockObject) {
            Dataset mInputDS = gdal.Open(mInput.getPath());
            Dataset mWatermaskDS = gdal.Open(mWatermask.getPath());
            Dataset mOutputDS = mInputDS.GetDriver().CreateCopy(mOutput.getPath(), mInputDS); // FIXME: create 32bit new raster instead?
            //mOutputDS.GetRasterBand(1).Fill(32767);

            assert(mInputDS.GetRasterCount() == 1);
            assert(mWatermaskDS.GetRasterCount() == 1);

            int rasterX = 0;
            int rasterY = 0;
            int rasterWidth = mInputDS.GetRasterXSize();
            int rasterHeight = mInputDS.GetRasterYSize();
            int rasterRight = rasterWidth;
            int rasterBottom = rasterHeight;

            Transformer transformer = new Transformer(mWatermaskDS, mInputDS, null);

            double[] point = new double[] {-0.5, -0.5, 0}; // Location of corner of first zone raster pixel

            transformer.TransformPoint(0, point);
            int watermaskX = (int) Math.round(point[0]);
            int watermaskY = (int) Math.round(point[1]);
            int watermaskWidth = mWatermaskDS.GetRasterXSize();
            int watermaskHeight = mWatermaskDS.GetRasterYSize();
            int watermaskRight = watermaskX + watermaskWidth;
            int watermaskBottom = watermaskY + watermaskHeight;

            int intersectX = Math.max(rasterX, watermaskX);
            int intersectY = Math.max(rasterY, watermaskY);
            int intersectRight = Math.min(rasterRight, watermaskRight);
            int intersectBottom = Math.min(rasterBottom, watermaskBottom);
            int intersectWidth = intersectRight - intersectX;
            int intersectHeight = intersectBottom - intersectY;

            double[] output = new double[intersectWidth];
            double[] watermask = new double[intersectWidth];
            for (int y=0; y<intersectHeight; y++) {
                mInputDS.GetRasterBand(1).ReadRaster(intersectX, intersectY + y, intersectWidth, 1, output);
                mWatermaskDS.GetRasterBand(1).ReadRaster(intersectX - watermaskX, intersectY - watermaskY + y, intersectWidth, 1, watermask);
                for (int x=0; x<intersectWidth; x++) {
                    if (watermask[x] == 0) {
                        output[x] = 32767; // FIXME: variable no data values
                    }
                }
                mOutputDS.GetRasterBand(1).WriteRaster(intersectX, intersectY + y, intersectWidth, 1, output);
            }

            mOutputDS.GetRasterBand(1).SetNoDataValue(32767);
            mOutputDS.GetRasterBand(1).ComputeStatistics(false);

            mInputDS.delete();
            mWatermaskDS.delete();
            mOutputDS.delete();
        }
    }

}
