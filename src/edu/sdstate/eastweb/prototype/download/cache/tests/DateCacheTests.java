package edu.sdstate.eastweb.prototype.download.cache.tests;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.cache.DateCache;
import edu.sdstate.eastweb.prototype.tests.*;
import static org.junit.Assert.*;

public class DateCacheTests extends MetadataTests {
    @Test
    public void test() throws IOException {
        File tempDictionary=new File(Config.getInstance().getTempDirectory());
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null,tempDictionary);
            try {
                final DataDate lastUpdated = DataDateTests.random();
                final DataDate startDate = DataDateTests.random();
                final List<DataDate> dates = DataDateTests.randomList();
                final DateCache ref = new DateCache(lastUpdated, startDate, dates);

                Collections.sort(dates);
                assertEquals(lastUpdated, ref.getLastUpdated());
                assertEquals(startDate, ref.getStartDate());
                assertEquals(dates, ref.getDates());

                ref.toFile(tempFile);
                assertEquals(ref, DateCache.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }
}