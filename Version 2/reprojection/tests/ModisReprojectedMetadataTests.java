package edu.sdstate.eastweb.prototype.reprojection.tests;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.download.tests.ModisDownloadMetadataTests;
import edu.sdstate.eastweb.prototype.reprojection.ModisReprojectedMetadata;
import edu.sdstate.eastweb.prototype.tests.*;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public class ModisReprojectedMetadataTests extends MetadataTests {
    @Test
    public void testFile() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final ModisReprojectedMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, ModisReprojectedMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final ModisReprojectedMetadata ref = random();
            assertEquals(ref, ModisReprojectedMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static ModisReprojectedMetadata random() {
        final List<ModisDownloadMetadata> downloads = ModisDownloadMetadataTests.randomList();
        final long timestamp = sRandom.nextLong();
        final ModisReprojectedMetadata obj = new ModisReprojectedMetadata(downloads, timestamp);

        Collections.sort(downloads);
        assertEquals(downloads, obj.getDownloads());
        assertEquals(timestamp, obj.getTimestamp());
        return obj;
    }

    public static List<ModisReprojectedMetadata> randomList() {
        final List<ModisReprojectedMetadata> list = new ArrayList<ModisReprojectedMetadata>();

        final int size = sRandom.nextInt(10) + 10;
        for (int i = 0; i < size; ++i) {
            list.add(random());
        }

        return list;
    }
}