package edu.sdstate.eastweb.prototype.reprojection.tests;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.download.EtoDownloadMetadata;
import edu.sdstate.eastweb.prototype.download.tests.EtoDownloadMetadataTests;
import edu.sdstate.eastweb.prototype.reprojection.EtoReprojectedMetadata;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public final class EtoReprojectedMetadataTests extends MetadataTests {
    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 100; ++i) {
            final EtoReprojectedMetadata ref = random();
            assertEquals(ref, EtoReprojectedMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testFile() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final EtoReprojectedMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, EtoReprojectedMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    public static EtoReprojectedMetadata random() {
        final EtoDownloadMetadata download = EtoDownloadMetadataTests.random();
        final long timestamp = sRandom.nextLong();
        final EtoReprojectedMetadata obj = new EtoReprojectedMetadata(download, timestamp);

        assertEquals(download, obj.getDownload());
        assertEquals(timestamp, obj.getTimestamp());
        return obj;
    }

    public static List<EtoReprojectedMetadata> randomList() {
        final List<EtoReprojectedMetadata> list = new ArrayList<EtoReprojectedMetadata>();

        final int size = sRandom.nextInt(10) + 10;
        for (int i = 0; i < size; ++i) {
            list.add(random());
        }

        return list;
    }
}