package edu.sdstate.eastweb.prototype.scheduler.tasks;
import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.NldasDownloadMetadata;
import edu.sdstate.eastweb.prototype.download.TrmmDownloadMetadata;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.reprojection.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

public class ProjectNldasTask implements RunnableTask{
    private static final long serialVersionUID = 1L;
    private final ProjectInfo mProject;
    private final DataDate mDate;
    public ProjectNldasTask(ProjectInfo project,  DataDate date) {
        mProject = project;
        mDate = date;
    }
    private File getInputFile() throws ConfigReadException {
        return DirectoryLayout.getNldasDownload(mDate);
    }

    private File getOutputFile() throws ConfigReadException {
        return DirectoryLayout.getNldasReprojected(mProject, mDate);
    }

    private File getMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getNldasReprojectedMetadata(mProject, mDate);
    }

    private NldasProjectedMetadata makeMetadata() throws IOException {
        final NldasDownloadMetadata download = NldasDownloadMetadata.fromFile(
                DirectoryLayout.getNldasDownloadMetadata(mDate));

        final long timestamp = new Date().getTime();

        return new NldasProjectedMetadata(download, timestamp);
    }

    @Override
    public boolean getCanSkip() {
        try {
            return NldasProjectedMetadata.fromFile(getMetadataFile()).equalsIgnoreTimestamp(makeMetadata());
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void run() throws Exception {
        File tmpFile = null;
        try {
            final File inFile = getInputFile();
            final File outFile = getOutputFile();
            FileUtils.forceMkdir(outFile.getParentFile());
            System.out.println(inFile.getPath());
            System.out.println(outFile.getPath());
            new NldasProjection().project(inFile,mProject, outFile);
        } finally {
            if (tmpFile != null && tmpFile.exists()) {
                FileUtils.deleteQuietly(tmpFile);
            }
        }

        // Write a metadata file
        final File metadataFile = getMetadataFile();
        FileUtils.forceMkdir(metadataFile.getParentFile());
        makeMetadata().toFile(metadataFile);
    }

    @Override
    public String getName() {
        return String.format(
                "Reproject Nldas: project=\"%s\", date=%s",
                mProject.getName(),
                mDate.toCompactString()
        );
    }

}
