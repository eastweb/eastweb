package version2.prototype.indices;

import java.io.File;

import version2.prototype.ConfigReadException;
import version2.prototype.DataDate;
import version2.prototype.DirectoryLayout;

public class GdalNLDASCalculator extends IndicesFramework{

    public GdalNLDASCalculator(String mProject, DataDate mDate, String feature, String mIndex ) throws ConfigReadException {
        setInputFiles(new File[] { getNldasReprojected(mProject, mDate) });
        setOutputFile(getIndex(mProject, mIndex, mDate, feature));
    }
    @Override
    protected double calculatePixelValue(double[] values) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    public File getNldasReprojected(String project, DataDate date)throws ConfigReadException{
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/%s.tif",
                DirectoryLayout.getRootDirectory(),
                project,
                "Nldas",
                date.getYear(),
                date.getDayOfYear(),
                "Nldas"
                ));
    }

    public File getIndex(String project, String index, DataDate date, String shapefile) throws ConfigReadException {
        String shapeFile = new File(shapefile).getName();
        return new File(String.format(
                "%s/projects/%s/indices/%s/%04d/%03d/%s/%s.tif",
                DirectoryLayout.getRootDirectory(),
                project,
                "ndlas",
                date.getYear(),
                date.getDayOfYear(),
                shapeFile,
                index
                ));
    }

}
