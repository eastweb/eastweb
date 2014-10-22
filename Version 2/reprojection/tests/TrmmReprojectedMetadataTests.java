package edu.sdstate.eastweb.prototype.reprojection.tests;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.download.TrmmDownloadMetadata;
import edu.sdstate.eastweb.prototype.download.tests.TrmmDownloadMetadataTests;
import edu.sdstate.eastweb.prototype.reprojection.TrmmReprojectedMetadata;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public class TrmmReprojectedMetadataTests extends MetadataTests {
    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final TrmmReprojectedMetadata ref = random();
            assertEquals(ref, TrmmReprojectedMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testFile() throws IOException {
        for (int i = 0; i < 1000; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final TrmmReprojectedMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, TrmmReprojectedMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    public static TrmmReprojectedMetadata random() {
        final TrmmDownloadMetadata download = TrmmDownloadMetadataTests.random();
        final long timestamp = sRandom.nextLong();
        final TrmmReprojectedMetadata obj = new TrmmReprojectedMetadata(download, timestamp);

        assertEquals(download, obj.getDownload());
        assertEquals(timestamp, obj.getTimestamp());
        return obj;
    }

    public static List<TrmmReprojectedMetadata> randomList() {
        final List<TrmmReprojectedMetadata> list = new ArrayList<TrmmReprojectedMetadata>();

        final int size = sRandom.nextInt(10) + 10;
        for (int i = 0; i < size; ++i) {
            list.add(random());
        }

        return list;
    }
}