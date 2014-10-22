package edu.sdstate.eastweb.prototype.download.tests;

import java.io.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.TrmmDownloadMetadata;
import edu.sdstate.eastweb.prototype.tests.DataDateTests;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public class TrmmDownloadMetadataTests extends MetadataTests {
    @Test
    public void testFile() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final TrmmDownloadMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, TrmmDownloadMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final TrmmDownloadMetadata ref = random();
            assertEquals(ref, TrmmDownloadMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static TrmmDownloadMetadata random() {
        final DataDate date = DataDateTests.random();
        final long timestamp = sRandom.nextLong();
        final TrmmDownloadMetadata obj = new TrmmDownloadMetadata(date, timestamp);

        assertEquals(date, obj.getDate());
        assertEquals(timestamp, obj.getTimestamp());
        return obj;
    }
}