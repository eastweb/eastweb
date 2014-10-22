package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.download.NldasDownloadMetadata;
import edu.sdstate.eastweb.prototype.download.NldasDownloader;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

public class DownloadNldasTask implements RunnableTask{
    private static final long serialVersionUID = 1L;
    private final DataDate mDate;

    public DownloadNldasTask( DataDate date) {
        mDate = date;
    }

    private File getMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getDownloadMetadata("Nldas", mDate);
    }

    private NldasDownloadMetadata makeMetadata() {
        return new NldasDownloadMetadata(mDate, new Date().getTime());
    }
    @Override
    public String getName() {
        return String.format(
                "Download Nldas: date=%s",
                mDate.toCompactString()
        );
    }

    @Override
    public void run() throws Exception {
        // Download the NLDAS dataset
        final File file = DirectoryLayout.getNldasDownload(mDate);
        FileUtils.forceMkdir(file.getParentFile());
        new NldasDownloader( mDate, file).download();

        // Write out the metadata
        final File metadataFile = getMetadataFile();
        FileUtils.forceMkdir(metadataFile.getParentFile());
        makeMetadata().toFile(metadataFile);

    }

    @Override
    public boolean getCanSkip() {
        try {
            return NldasDownloadMetadata.fromFile(getMetadataFile()).equalsIgnoreTimestamp(makeMetadata());
        } catch (IOException e) {
            return false;
        }
    }

}
