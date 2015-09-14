package edu.sdstate.eastweb.prototype.indices;

import java.io.File;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

public abstract class GdalSimpleIndexCalculator implements IndexCalculator {

    private static final float OUTPUT_NODATA = Float.intBitsToFloat(0xff7fffff);

    private File[] mInputFiles;
    private File mOutputFile;

    protected void setInputFiles(File[] inputFiles) {
        assert (inputFiles.length > 0);
        mInputFiles = inputFiles;
    }

    protected void setOutputFile(File outputFile) {
        mOutputFile = outputFile;
    }

    private Dataset createOutput(Dataset[] inputs) {
        Dataset outputDS =
                gdal.GetDriverByName("GTiff").Create(mOutputFile.getPath(),
                        inputs[0].GetRasterXSize(), inputs[0].GetRasterYSize(),
                        1, gdalconst.GDT_Float32);

        outputDS.SetGeoTransform(inputs[0].GetGeoTransform());
        outputDS.SetProjection(inputs[0].GetProjection());
        outputDS.SetMetadata(inputs[0].GetMetadata_Dict());

        return outputDS;
    }

    @Override
    public void calculate() throws Exception {
        GdalUtils.register();

        synchronized (GdalUtils.lockObject) {
            // Setup the output and inputs
            Dataset[] inputs = new Dataset[mInputFiles.length];
            for (int i = 0; i < mInputFiles.length; i++) {
                inputs[i] = gdal.Open(mInputFiles[i].getPath());
            }
            Dataset outputDS = createOutput(inputs);

            // Process the output and inputs
            process(inputs, outputDS);

            // Calculate statistics
            for (int i = 1; i <= outputDS.GetRasterCount(); i++) {
                Band band = outputDS.GetRasterBand(i);

                band.SetNoDataValue(OUTPUT_NODATA);
                band.ComputeStatistics(false);
            }

            // Close and flush output and inputs
            for (Dataset input : inputs) {
                input.delete();
            }
            outputDS.delete();
        }
    }

    /**
     * @param inputs
     * @param output
     * @throws Exception
     */
    private void process(Dataset[] inputs, Dataset output) throws Exception {
        int xSize = inputs[0].GetRasterXSize();
        int ySize = inputs[0].GetRasterYSize();

        double[][] inputsArray = new double[inputs.length][xSize];
        double[] outputArray = new double[xSize];

        for (int y = 0; y < ySize; y++) {
            for (int i = 0; i < inputs.length; i++) {
                inputs[i].GetRasterBand(1).ReadRaster(0, y, xSize, 1,
                        inputsArray[i]);
            }

            for (int x = 0; x < xSize; x++) {
                double[] values = new double[inputs.length];
                for (int i = 0; i < inputs.length; i++) {
                    values[i] = inputsArray[i][x];
                }

                outputArray[x] = calculatePixelValue(values);
            }

            output.GetRasterBand(1).WriteRaster(0, y, xSize, 1, outputArray);
        }

    }

    /**
     * @param values
     * @return
     */
    protected abstract double calculatePixelValue(double[] values)
            throws Exception;

}
