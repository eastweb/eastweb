package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

public class DownloadTrmmTask implements RunnableTask {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final TrmmProduct mProduct;
    private final DataDate mDate;

    public DownloadTrmmTask(TrmmProduct product, DataDate date) {
        mProduct = product;
        mDate = date;
    }

    private File getOutputFile() throws ConfigReadException {
        return DirectoryLayout.getTrmmDownload(mProduct, mDate);
    }

    private File getMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getTrmmDownloadMetadata(mProduct, mDate);
    }

    private TrmmDownloadMetadata makeMetadata() {
        return new TrmmDownloadMetadata(mDate, new Date().getTime());
    }

    @Override
    public boolean getCanSkip() {
        try {
            return TrmmDownloadMetadata.fromFile(getMetadataFile()).equalsIgnoreTimestamp(makeMetadata());
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void run() throws Exception {
        // Download the TRMM dataset
        final File file = DirectoryLayout.getTrmmDownload(mProduct, mDate);
        FileUtils.forceMkdir(file.getParentFile());
        new TrmmDownloader(mProduct, mDate, file).download();

        // Write out the metadata
        final File metadataFile = getMetadataFile();
        FileUtils.forceMkdir(metadataFile.getParentFile());
        makeMetadata().toFile(metadataFile);
    }

    @Override
    public String getName() {
        return String.format(
                "Download TRMM: date=%s",
                mDate.toCompactString()
        );
    }

}