package version2.prototype.projection;

import java.io.File;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;

import version2.prototype.util.GdalUtils;

public abstract class Filter {
    private final File mInput;
    private final File mOutput;

    public Filter(File input, File output) {
        assert(input.exists());
        assert(!output.exists());
        assert(!input.equals(output));

        mInput = input;
        mOutput = output;
    }

    public void filter() throws Exception {
        GdalUtils.register();

        synchronized (GdalUtils.lockObject) {
            Dataset inputDS = gdal.Open(mInput.getPath());
            assert(inputDS.GetRasterCount() == 1);

            Dataset outputDS = createOutput(inputDS);
            double[] array = new double[outputDS.GetRasterXSize()];

            for (int y=0; y<outputDS.GetRasterYSize(); y++) {
                outputDS.GetRasterBand(1).ReadRaster(0, y, outputDS.GetRasterXSize(), 1, array);

                for (int x=0; x<outputDS.GetRasterXSize(); x++) {
                    array[x] = filterValue(array[x]);
                }

                synchronized (GdalUtils.lockObject) {
                    outputDS.GetRasterBand(1).WriteRaster(0, y, outputDS.GetRasterXSize(), 1, array);
                }
            }

            inputDS.delete();
            outputDS.delete();
        }
    }

    protected Dataset createOutput(Dataset inputDS) {
        return gdal.GetDriverByName("GTiff").CreateCopy(mOutput.getPath(), inputDS);
    }

    protected abstract double filterValue(double value);
}
