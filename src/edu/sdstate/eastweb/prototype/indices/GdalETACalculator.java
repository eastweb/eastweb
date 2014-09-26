package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.Transformer;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class GdalETACalculator extends IndicesFramework {

    final File mLst; // Daytime LST
    final File mElevation;
    final File mEto;
    final File mEta;
    final double mMinLst;
    final double mMaxLst;

    public GdalETACalculator(ProjectInfo mProject, DataDate mDate, String feature, EnvironmentalIndex mIndex ) throws ConfigReadException {
        mLst = DirectoryLayout.getModisClip(mProject, mDate,
                ModisProduct.LST, feature, "LST_Day_1km");
        mElevation = new File(DirectoryLayout.getSettingsDirectory(mProject),
                mProject.getElevation());
        mEto = DirectoryLayout.getEtoReprojected(mProject, mDate);
        mEta = DirectoryLayout.getIndex(mProject, mIndex, mDate, feature);
        mMinLst = mProject.getMinLst() + 273.15;
        mMaxLst = mProject.getMaxLst() + 273.15;
    }

    @Override
    public void calculate() throws Exception {
        GdalUtils.register();

        synchronized (GdalUtils.lockObject) {

            // Calculate corrected LST
            Dataset lstDS = gdal.Open(mLst.getPath());

            final int WIDTH = lstDS.GetRasterXSize();
            final int HEIGHT = lstDS.GetRasterYSize();

            Dataset elevationDS = gdal.Open(mElevation.getPath());
            Dataset correctedLstDS =
                    gdal.GetDriverByName("GTiff").Create(
                            new File(mEta.getParent(), "clst.tif").getPath(), // FIXME
                            WIDTH, HEIGHT, 1, gdalconst.GDT_Float32);
            Dataset etfDS =
                    gdal.GetDriverByName("GTiff").Create(
                            new File(mEta.getParent(), "etf.tif").getPath(), // FIXME
                            WIDTH, HEIGHT, 1, gdalconst.GDT_Float32);
            Dataset etoDS = gdal.Open(mEto.getPath());
            Dataset etaDS =
                    gdal.GetDriverByName("GTiff").Create(mEta.getPath(), WIDTH,
                            HEIGHT, 1, gdalconst.GDT_Float32);
            etaDS.SetProjection(lstDS.GetProjection());
            etaDS.SetGeoTransform(lstDS.GetGeoTransform());

            assert (lstDS.GetRasterXSize() == elevationDS.GetRasterXSize());
            assert (lstDS.GetRasterYSize() == elevationDS.GetRasterYSize());

            assert (lstDS.GetRasterYSize() == etoDS.GetRasterXSize());
            assert (lstDS.GetRasterYSize() == etoDS.GetRasterYSize());

            Transformer transformer = new Transformer(lstDS, elevationDS, null);

            // Location of corner of first zone raster pixel
            double[] point = new double[] { -0.5, -0.5, 0 };

            transformer.TransformPoint(0, point);
            int xOffset = (int) Math.round(point[0]);
            int yOffset = (int) Math.round(point[1]);

            double[] lstArray = new double[WIDTH];
            double[] elevationArray = new double[WIDTH];
            double[] correctedArray = new double[WIDTH];
            for (int y = 0; y < HEIGHT; y++) {
                lstDS.GetRasterBand(1).ReadRaster(0, y, WIDTH, 1, lstArray);
                elevationDS.GetRasterBand(1).ReadRaster(xOffset, yOffset + y,
                        WIDTH, 1, elevationArray);
                for (int x = 0; x < WIDTH; x++) {
                    if (lstArray[x] != 32767 && lstArray[x] > mMinLst
                            && lstArray[x] < mMaxLst
                            && elevationArray[x] != -3.4028234663852886E38) {
                        // FIXME: assumes elevation hasn't been corrected yet
                        correctedArray[x] =
                                lstArray[x] + (elevationArray[x] * 0.0065);
                    } else {
                        correctedArray[x] = 0;
                    }
                }
                correctedLstDS.GetRasterBand(1).WriteRaster(0, y, WIDTH, 1,
                        correctedArray);
            }

            // Calculate ETf
            final double[] min = new double[1];
            final double[] max = new double[1];
            correctedLstDS.GetRasterBand(1).SetNoDataValue(0);
            correctedLstDS.GetRasterBand(1).ComputeStatistics(false, min, max);
            final double hot = max[0];
            final double cold = min[0];

            double[] etfArray = new double[WIDTH];
            for (int y = 0; y < HEIGHT; y++) {
                correctedLstDS.GetRasterBand(1).ReadRaster(0, y, WIDTH, 1,
                        correctedArray);
                for (int x = 0; x < WIDTH; x++) {
                    if (correctedArray[x] != 0) {
                        etfArray[x] = (hot - correctedArray[x]) / (hot - cold);
                    } else {
                        etfArray[x] = 0;
                    }
                }
                etfDS.GetRasterBand(1).WriteRaster(0, y, WIDTH, 1, etfArray);
            }

            // Calculate ETa
            double[] etoArray = new double[WIDTH];
            double[] etaArray = new double[WIDTH];
            for (int y = 0; y < HEIGHT; y++) {
                etfDS.GetRasterBand(1).ReadRaster(0, y, WIDTH, 1, etfArray);
                etoDS.GetRasterBand(1).ReadRaster(0, y, WIDTH, 1, etoArray);
                for (int x = 0; x < WIDTH; x++) {
                    if (etfArray[x] != 0) {
                        etaArray[x] = etfArray[x] * etoArray[x];
                    } else {
                        etaArray[x] = 0;
                    }
                }
                etaDS.GetRasterBand(1).WriteRaster(0, y, WIDTH, 1, etaArray);
            }

            // Save and cleanup
            correctedLstDS.SetProjection(lstDS.GetProjection());
            correctedLstDS.GetRasterBand(1).SetNoDataValue(0);
            correctedLstDS.GetRasterBand(1).ComputeStatistics(false);
            etfDS.SetProjection(lstDS.GetProjection());
            etfDS.GetRasterBand(1).SetNoDataValue(0);
            etfDS.GetRasterBand(1).ComputeStatistics(false);
            etaDS.GetRasterBand(1).SetNoDataValue(0);
            etaDS.GetRasterBand(1).ComputeStatistics(false);

            elevationDS.delete();
            correctedLstDS.delete();
            etfDS.delete();
            etoDS.delete();
            etaDS.delete();
        }
    }

    @Override
    protected double calculatePixelValue(double[] values) throws Exception {
        throw new Exception();
    }

}
