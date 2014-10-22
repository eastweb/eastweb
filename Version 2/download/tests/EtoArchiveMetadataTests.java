package edu.sdstate.eastweb.prototype.download.tests;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.tests.DataDateTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public class EtoArchiveMetadataTests {
    @Test
    public void test() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("temp", null);
            try {
                final EtoArchiveMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, EtoArchiveMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final EtoArchiveMetadata ref = random();
            assertEquals(ref, EtoArchiveMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static EtoArchiveMetadata random() {
        final EtoArchive archive = EtoArchiveTests.random();
        final List<DataDate> dates = DataDateTests.randomList();
        final DataDate downloaded = DataDateTests.random();
        final EtoArchiveMetadata obj = new EtoArchiveMetadata(archive, dates, downloaded);

        Collections.sort(dates);
        assertEquals(archive, obj.getArchive());
        assertEquals(dates, obj.getDates());
        assertEquals(downloaded, obj.getDownloaded());
        return obj;
    }
}