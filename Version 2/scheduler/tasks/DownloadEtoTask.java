package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.CallableTask;

public class DownloadEtoTask implements CallableTask<List<DataDate>> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final EtoArchive mArchive;

    public DownloadEtoTask(EtoArchive archive) {
        mArchive = archive;
    }

    private File getArchiveMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getEtoArchiveMetadata(mArchive);
    }

    private void writeDownloadMetadata(List<DataDate> dates) throws IOException {
        for (DataDate date : dates) {
            final File file = DirectoryLayout.getEtoDownloadMetadata(date);
            FileUtils.forceMkdir(file.getParentFile());
            new EtoDownloadMetadata(date, DataDate.today()).toFile(file);
        }
    }

    @Override
    public List<DataDate> getCanSkip() {
        try {
            final EtoArchiveMetadata metadata = EtoArchiveMetadata.fromFile(
                    getArchiveMetadataFile());

            return metadata.getDates();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public List<DataDate> call() throws Exception {
        // Download and extract the archive
        EtoDownloader ed=new EtoDownloader(mArchive);
        ed.download();
        final List<DataDate> dates =ed.getDates() ;
        // Write the archive metadata file
        final File metadataFile = getArchiveMetadataFile();
        FileUtils.forceMkdir(metadataFile.getParentFile());
        new EtoArchiveMetadata(mArchive, dates, DataDate.today()).toFile(metadataFile);

        // Write download metadata files
        writeDownloadMetadata(dates);

        return dates;
    }

    @Override
    public String getName() {
        final StringBuilder sb = new StringBuilder()
        .append("Download ETo: year=").append(mArchive.getYear());

        if (mArchive.getType().getHasMonth()) {
            sb.append(", month=").append(mArchive.getMonth());
        }

        if (mArchive.getType().getHasDay()) {
            sb.append(", day=").append(mArchive.getDay());
        }

        return sb.toString();
    }

}