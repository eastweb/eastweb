package edu.sdstate.eastweb.prototype.download.tests;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.tests.*;
import static org.junit.Assert.*;

public class ModisDownloadMetadataTests extends MetadataTests {
    @Test
    public void test() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final ModisDownloadMetadata ref = random();
                ref.toFile(tempFile);
                assertEquals(ref, ModisDownloadMetadata.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static ModisDownloadMetadata random() {
        final ModisId modisId = ModisIdTests.random();
        final DataDate downloaded = DataDateTests.random();
        final ModisDownloadMetadata obj = new ModisDownloadMetadata(modisId, downloaded);

        assertEquals(modisId, obj.getModisId());
        assertEquals(downloaded, obj.getDownloaded());
        return obj;
    }

    public static List<ModisDownloadMetadata> randomList() {
        final List<ModisDownloadMetadata> list = new ArrayList<ModisDownloadMetadata>();

        final int size = sRandom.nextInt(10) + 10;
        for (int i = 0; i < size; ++i) {
            list.add(random());
        }

        return list;
    }
}