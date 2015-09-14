package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.download.cache.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.CallableTask;

public final class UpdateModisDateCacheTask implements CallableTask<DateCache> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final ModisProduct mProduct;
    private final DataDate mStartDate;

    public UpdateModisDateCacheTask(ModisProduct product,
            DataDate startDate) {
        mProduct = product;
        mStartDate = startDate;
    }

    private File getFile() throws ConfigReadException {
        return DirectoryLayout.getModisDateCache(mProduct);
    }

    @Override
    public DateCache getCanSkip() {
        try {
            final DateCache cache = DateCache.fromFile(getFile());

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
    public DateCache call() throws IOException {
        // Get a list of dates and construct a date cache
        final DateCache cache = new DateCache(
                DataDate.today(),
                mStartDate,
                ModisDownloader.listDates(mProduct, mStartDate)
                );

        // Write out the date cache
        final File file = getFile();
        FileUtils.forceMkdir(file.getParentFile());
        cache.toFile(file);

        return cache;
    }

    @Override
    public String getName() {
        return String.format(
                "Update MODIS date cache: product=%s, startDate=%s",
                mProduct,
                mStartDate.toCompactString()
                );
    }

}