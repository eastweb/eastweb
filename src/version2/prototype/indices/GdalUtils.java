package version2.prototype.indices;

import java.io.IOException;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.ogr;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.Projection.ResamplingType;

public class GdalUtils {
    private GdalUtils() {
    }

    /**
     * All GDAL operations should be done while holding a lock on this object.
     * GDAL is "not competely thread-safe", so this may be critical.
     */
    public static final Object lockObject = new Object();

    private static boolean sRegistered = false;

    public static void register() {
        synchronized (lockObject) {
            if (!sRegistered) {
                ogr.RegisterAll();
                gdal.AllRegister();
                ogr.UseExceptions();
                sRegistered = true;
            }
        }
    }

    /**
     * Checks for exceptions reported to the GDAL error reporting system and
     * maps them to Java exceptions or errors.
     * 
     * @throws IOException
     *             CPLE_AppDefined, CPLE_FileIO, CPLE_OpenFailed,
     *             CPLE_NoWriteAccess, CPLE_UserInterrupt
     * @throws IllegalArgumentException
     *             CPLE_IllegalArg
     * @throws UnsupportedOperationException
     *             CPLE_NotSupported
     */
    public static void errorCheck() throws IOException,
    IllegalArgumentException, UnsupportedOperationException {
        synchronized (lockObject) {
            int type = gdal.GetLastErrorType();
            if (type != gdalconst.CE_None) {
                int number = gdal.GetLastErrorNo();
                String message = gdal.GetLastErrorMsg();
                gdal.ErrorReset();

                if (number == gdalconst.CPLE_AppDefined
                        || number == gdalconst.CPLE_FileIO
                        || number == gdalconst.CPLE_OpenFailed
                        || number == gdalconst.CPLE_NoWriteAccess
                        || number == gdalconst.CPLE_UserInterrupt) {
                    throw new IOException(message);
                } else if (number == gdalconst.CPLE_OutOfMemory) {
                    throw new OutOfMemoryError(message);
                } else if (number == gdalconst.CPLE_IllegalArg) {
                    throw new IllegalArgumentException(message);
                } else if (number == gdalconst.CPLE_NotSupported) {
                    throw new UnsupportedOperationException(message);
                } else if (number == gdalconst.CPLE_AssertionFailed) {
                    throw new AssertionError(message);
                }
            }
        }
    }

    /**
     * Do the projection for input file, and write the processed data into
     * output file
     * 
     * @param wkt
     *            wkt string contains the projection information for the output
     *            file
     * @param input
     *            input file.
     * @param project
     *            store the shape file and project information
     * @param output
     *            output file.
     * @param resampleAlg
     *            the type of resampling to use, among gdalconst.GRA_
     * @throws ConfigReadException
     *             *
     **/
    public static void project(File input, ProjectInfo project,
            File output) throws ConfigReadException {
        assert (project.getShapeFiles().size() > 0);

        GdalUtils.register();

        synchronized (GdalUtils.lockObject) {
            // Load input file and features
            Dataset inputDS = gdal.Open(input.getPath());
            // System.out.println(inputDS.GetProjectionRef().toString());
            // SpatialReference inputRef = new SpatialReference();
            List<DataSource> features = new ArrayList<DataSource>();
            for (String filename : project.getShapeFiles()) {
                features.add(ogr.Open(new File(DirectoryLayout
                        .getSettingsDirectory(project), filename).getPath()));
            }

            // Find union of extents
            double[] extent = features.get(0).GetLayer(0).GetExtent(); // Ordered:
            // left,
            // right,
            // bottom,
            // top
            // System.out.println(Arrays.toString(extent));
            double left = extent[0];
            double right = extent[1];
            double bottom = extent[2];
            double top = extent[3];
            for (int i = 1; i < features.size(); i++) {
                extent = features.get(i).GetLayer(0).GetExtent();
                if (extent[0] < left) {
                    left = extent[0];
                } else if (extent[1] > right) {
                    right = extent[1];
                } else if (extent[2] < bottom) {
                    bottom = extent[2];
                } else if (extent[3] > top) {
                    top = extent[3];
                }
            }

            // Project to union of extents
            Dataset outputDS =
                    gdal.GetDriverByName("GTiff").Create(
                            output.getPath(),
                            (int) Math.ceil((right - left)
                                    / project.getProjection().getPixelSize()),
                                    (int) Math.ceil((top - bottom)
                                            / project.getProjection().getPixelSize()),
                                            1, gdalconst.GDT_Float32);

            // TODO: get projection from project info, and get transform from
            // shape file
            // SpatialReference outputRef = new SpatialReference();
            // outputRef.ImportFromWkt(wkt);
            String outputProjection =
                    features.get(0).GetLayer(0).GetSpatialRef().ExportToWkt();
            outputDS.SetProjection(outputProjection);
            outputDS.SetGeoTransform(new double[] { left,
                    project.getProjection().getPixelSize(), 0, top, 0,
                    -project.getProjection().getPixelSize() });

            // get resample argument
            int resampleAlg = -1;
            ResamplingType resample =
                    project.getProjection().getResamplingType();
            switch (resample) {
            case NEAREST_NEIGHBOR:
                resampleAlg = gdalconst.GRA_NearestNeighbour;
            case BILINEAR:
                resampleAlg = gdalconst.GRA_Bilinear;
            case CUBIC_CONVOLUTION:
                resampleAlg = gdalconst.GRA_CubicSpline;
            }
            gdal.ReprojectImage(inputDS, outputDS, null, null, resampleAlg);
            outputDS.GetRasterBand(1).ComputeStatistics(false);
            outputDS.delete();
            inputDS.delete();
        }
    }
}
