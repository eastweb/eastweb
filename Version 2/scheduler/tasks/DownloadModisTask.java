package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

public class DownloadModisTask implements RunnableTask {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final ModisId mModisId;

    public DownloadModisTask(ModisId modisId) {
        mModisId = modisId;
    }

    private File getOutputFile() throws ConfigReadException {
        return DirectoryLayout.getModisDownload(mModisId.getProduct(), mModisId.getDate(),
                mModisId.getTile());
    }

    private File getMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getModisDownloadMetadata(mModisId.getProduct(), mModisId.getDate(),
                mModisId.getTile());
    }

    private ModisDownloadMetadata makeMetadata() {
        return new ModisDownloadMetadata(mModisId, DataDate.today());
    }

    @Override
    public boolean getCanSkip() {
        try {
            return ModisDownloadMetadata.fromFile(getMetadataFile()).equalsIgnoreDownloaded(makeMetadata());
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void run() throws IOException, ConfigReadException {
        // Download the MODIS tile
        FileUtils.forceMkdir(getOutputFile().getParentFile());
        ModisDownloader.newWithProduct(mModisId).download();

        // Write out the metadata
        final File metadataFile = getMetadataFile();
        FileUtils.forceMkdir(metadataFile.getParentFile());
        makeMetadata().toFile(metadataFile);
    }

    @Override
    public String getName() {
        return String.format(
                "Download MODIS: product=%s, date=%s, tile=%s, processed=%s",
                mModisId.getProduct(),
                mModisId.getDate().toCompactString(),
                mModisId.getTile().toCompactString(),
                mModisId.getProcessed().toCompactString()
        );
    }

}