package edu.sdstate.eastweb.prototype.indices.tests;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.indices.IndexMetadata;
import edu.sdstate.eastweb.prototype.reprojection.*;
import edu.sdstate.eastweb.prototype.reprojection.tests.*;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public final class IndexMetadataTests extends MetadataTests {
    @Test
    public void testRandom() throws IOException {
        for (int i = 0; i < 1000; ++i) {
            random();
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final IndexMetadata ref = random();
            assertEquals(ref, IndexMetadata.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testFile() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final IndexMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, IndexMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    public static IndexMetadata random() {
        final List<ModisReprojectedMetadata> modis = ModisReprojectedMetadataTests.randomList();
        final List<TrmmReprojectedMetadata> trmm = TrmmReprojectedMetadataTests.randomList();
        final List<EtoReprojectedMetadata> eto = EtoReprojectedMetadataTests.randomList();
        final int zonalSummaryId = sRandom.nextInt();
        final long timestamp = sRandom.nextLong();
        final IndexMetadata obj = new IndexMetadata(modis, trmm, eto, "ngp", timestamp);

        Collections.sort(modis);
        Collections.sort(trmm);
        Collections.sort(eto);
        assertEquals(modis, obj.getModis());
        assertEquals(trmm, obj.getTrmm());
        assertEquals(eto, obj.getEto());
        //assertEquals(zonalSummaryId, obj.getZonalSummaryId());
        assertEquals(timestamp, obj.getTimestamp());
        return obj;
    }
}