package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.NldasDownloader;
import edu.sdstate.eastweb.prototype.download.TrmmDownloader;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.download.cache.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.CallableTask;

public final class UpdateNldasDateCacheTask implements CallableTask<DateCache> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final DataDate mStartDate;

    public UpdateNldasDateCacheTask( DataDate startDate) {
        mStartDate = startDate;
    }

    private File getFile() throws ConfigReadException {
        return DirectoryLayout.getDateCache("Nldas");
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
        DateCache cache=null;
        try {
            cache = new DateCache(DataDate.today(), mStartDate,
                    NldasDownloader.listDates( mStartDate));
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Write out the date cache
        final File file = getFile();
        FileUtils.forceMkdir(file.getParentFile());
        cache.toFile(file);
        return cache;
    }

    @Override
    public String getName() {
        return String.format("Update Nldas date cache: startDate=%s",
                mStartDate.toCompactString());
    }

}
