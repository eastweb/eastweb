package edu.sdstate.eastweb.prototype.download.cache.tests;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.EtoArchive;
import edu.sdstate.eastweb.prototype.download.cache.EtoArchiveCache;
import edu.sdstate.eastweb.prototype.download.tests.EtoArchiveTests;
import edu.sdstate.eastweb.prototype.tests.DataDateTests;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;
import static org.junit.Assert.*;

public class EtoArchiveCacheTests extends MetadataTests {
    @Test
    public void test() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final DataDate lastUpdated = DataDateTests.random();
                final DataDate startDate = DataDateTests.random();
                final List<EtoArchive> archives = EtoArchiveTests.randomList();
                final EtoArchiveCache ref = new EtoArchiveCache(lastUpdated, startDate, archives);

                Collections.sort(archives);
                assertEquals(lastUpdated, ref.getLastUpdated());
                assertEquals(startDate, ref.getStartDate());
                assertEquals(archives, ref.getArchives());

                ref.toFile(tempFile);
                assertEquals(ref, EtoArchiveCache.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }
}