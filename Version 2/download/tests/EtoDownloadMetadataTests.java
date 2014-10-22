package edu.sdstate.eastweb.prototype.download.tests;

import java.io.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.tests.DataDateTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public class EtoDownloadMetadataTests {
    @Test
    public void testFile() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final EtoDownloadMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, EtoDownloadMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final EtoDownloadMetadata ref = random();
            assertEquals(ref, EtoDownloadMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static EtoDownloadMetadata random() {
        final DataDate date = DataDateTests.random();
        final DataDate downloaded = DataDateTests.random();
        final EtoDownloadMetadata obj = new EtoDownloadMetadata(date, downloaded);

        assertEquals(date, obj.getDate());
        assertEquals(downloaded, obj.getDownloaded());
        return obj;
    }
}