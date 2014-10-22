package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.TrmmDownloadMetadata;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.reprojection.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

public class GdalProjectTrmmTask implements RunnableTask {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final ProjectInfo mProject;
    private final TrmmProduct mProduct;
    private final DataDate mDate;

    public GdalProjectTrmmTask(ProjectInfo project, TrmmProduct product, DataDate date) {
        mProject = project;
        mProduct = product;
        mDate = date;
    }

    private File getInputFile() throws ConfigReadException {
        return DirectoryLayout.getTrmmDownload(mProduct, mDate);
    }

    private File getOutputFile() throws ConfigReadException {
        return DirectoryLayout.getTrmmReprojected(mProject, mProduct, mDate);
    }

    private File getMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getTrmmReprojectedMetadata(mProject, mProduct, mDate);
    }

    private TrmmReprojectedMetadata makeMetadata() throws IOException {
        final TrmmDownloadMetadata download = TrmmDownloadMetadata.fromFile(
                DirectoryLayout.getTrmmDownloadMetadata(mProduct, mDate));

        final long timestamp = new Date().getTime();

        return new TrmmReprojectedMetadata(download, timestamp);
    }

    @Override
    public boolean getCanSkip() {
        try {
            return TrmmReprojectedMetadata.fromFile(getMetadataFile()).equalsIgnoreTimestamp(makeMetadata());
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void run() throws Exception {
        File tmpFile = null;
        try {
            final File inFile = getInputFile();
            tmpFile = new File(getOutputFile().getPath() + ".tmp");
            final File outFile = getOutputFile();
            FileUtils.forceMkdir(outFile.getParentFile());
            new GdalTrmmConvert(mProduct, inFile, tmpFile).convert();
            new TrmmProjection().project(tmpFile, mProject, outFile);
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
                "Reproject TRMM: project=\"%s\", date=%s",
                mProject.getName(),
                mDate.toCompactString()
        );
    }

}