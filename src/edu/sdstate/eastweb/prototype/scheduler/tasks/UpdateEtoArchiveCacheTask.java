package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.EtoDownloader;
import edu.sdstate.eastweb.prototype.download.cache.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.CallableTask;

public final class UpdateEtoArchiveCacheTask implements CallableTask<EtoArchiveCache> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final DataDate mStartDate;

    public UpdateEtoArchiveCacheTask(DataDate startDate) {
        mStartDate = startDate;
    }

    private File getFile() throws ConfigReadException {
        return DirectoryLayout.getEtoArchiveCache();
    }

    @Override
    public EtoArchiveCache getCanSkip() {
        try {
            final EtoArchiveCache cache = EtoArchiveCache.fromFile(getFile());

            // Check freshness
            if (!CacheUtils.isFresh(cache)) {
                return null;
            }

            // Check start date
            if (!cache.getStartDate().equals(mStartDate)) {
                return null;
            }

            return cache;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public EtoArchiveCache call() throws IOException {
        // Get a list of archives and construct an archive cache
        final EtoArchiveCache cache = new EtoArchiveCache(
                DataDate.today(),
                mStartDate,
                EtoDownloader.listArchives(mStartDate)
        );

        // Write out the archive cache
        final File file = getFile();
        FileUtils.forceMkdir(file.getParentFile());
        cache.toFile(file);

        return cache;
    }

    @Override
    public String getName() {
        return String.format(
                "Update ETo archive cache: startDate=%s",
                mStartDate.toCompactString()
        );
    }

}