package version2.prototype.projection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import version2.prototype.ConfigReadException;
import version2.prototype.DataDate;
import version2.prototype.DirectoryLayout;
import version2.prototype.ModisTile;
import version2.prototype.ProjectInfo;

public class PrepareProcessTask {
    private final ProjectInfo mProject;
    private final DataDate mDate;
    private String mProduct;

    public PrepareProcessTask(ProjectInfo project, String mProduct, DataDate date) {
        this.mProduct = mProduct;
        mProject = project;
        mDate = date;
    }

    public File getMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getModisReprojectedMetadata(mProject, mProduct, mDate);
    }

    public File getInputFile() throws ConfigReadException {
        return DirectoryLayout.getNldasDownload(mDate);
    }

    public File getOutputFile() throws ConfigReadException {
        return DirectoryLayout.getNldasReprojected(mProject, mDate);
    }

    public File[] getInputFiles() throws ConfigReadException {
        final List<File> files = new ArrayList<File>();

        for (ModisTile tile : mProject.getModisTiles()) {
            files.add(DirectoryLayout.getModisDownload(mProduct, mDate, tile));
        }
        return files.toArray(new File[0]);
    }

    public int[] getBands() {

        if(mProduct == "NBAR")
        {
            return new int[] { 1,2, 3, 4, 5, 6, 7 };
        }
        else if (mProduct == "LST")
        {
            return new int[] { 1,5 };
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
