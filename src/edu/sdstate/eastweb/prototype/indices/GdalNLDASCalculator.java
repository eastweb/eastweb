package edu.sdstate.eastweb.prototype.indices;
import java.io.File;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;

public class GdalNLDASCalculator extends IndicesFramework{


    public GdalNLDASCalculator(ProjectInfo mProject, DataDate mDate, String feature, EnvironmentalIndex mIndex ) throws ConfigReadException {
        setInputFiles(new File[] { DirectoryLayout.getNldasReprojected(mProject,
                mDate) });
        setOutputFile(DirectoryLayout.getIndex(mProject, mIndex, mDate, feature));
    }
    @Override
    protected double calculatePixelValue(double[] values) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

}
