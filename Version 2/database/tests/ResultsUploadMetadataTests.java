package edu.sdstate.eastweb.prototype.database.tests;

import java.io.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.database.ResultsUploadMetadata;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import edu.sdstate.eastweb.prototype.zonalstatistics.ZonalStatisticsMetadata;
import edu.sdstate.eastweb.prototype.zonalstatistics.tests.ZonalStatisticsMetadataTests;
import static org.junit.Assert.*;

public final class ResultsUploadMetadataTests extends MetadataTests{
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
            final ResultsUploadMetadata ref = random();
            assertEquals(ref, ResultsUploadMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testFile() throws IOException {
        for (int i = 0; i < 50; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final ResultsUploadMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, ResultsUploadMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    public static ResultsUploadMetadata random() {
        final ZonalStatisticsMetadata zonalStatistics = ZonalStatisticsMetadataTests.random();
        final long timestamp = sRandom.nextLong();
        final ResultsUploadMetadata obj = new ResultsUploadMetadata(zonalStatistics, timestamp);

        assertEquals(zonalStatistics, obj.getZonalStatistics());
        assertEquals(timestamp, obj.getTimestamp());
        return obj;
    }

    public static void main(String[] args) throws Exception {
        ResultsUploadMetadataTests t = new ResultsUploadMetadataTests();
        t.testRandom();
    }
}