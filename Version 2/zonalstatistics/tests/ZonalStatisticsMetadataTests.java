package edu.sdstate.eastweb.prototype.zonalstatistics.tests;

import java.io.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.indices.IndexMetadata;
import edu.sdstate.eastweb.prototype.indices.tests.IndexMetadataTests;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import edu.sdstate.eastweb.prototype.zonalstatistics.ZonalStatisticsMetadata;
import static org.junit.Assert.*;

public class ZonalStatisticsMetadataTests extends MetadataTests {
    @Test
    public void testRandom() {
        for (int i = 0; i < 1000; ++i) {
            random();
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 50; ++i) {
            final ZonalStatisticsMetadata ref = random();
            assertEquals(ref, ZonalStatisticsMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testFile() throws IOException {
        for (int i = 0; i < 50; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final ZonalStatisticsMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, ZonalStatisticsMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    public static ZonalStatisticsMetadata random() {
        final IndexMetadata index = IndexMetadataTests.random();
        final long timestamp = sRandom.nextLong();
        final ZonalStatisticsMetadata obj = new ZonalStatisticsMetadata(index, "ngp", timestamp);

        assertEquals(index, obj.getIndex());
        assertEquals(timestamp, obj.getTimestamp());
        return obj;
    }
}