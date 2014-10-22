package edu.sdstate.eastweb.prototype.download.cache.tests;

import java.io.*;
import java.util.*;
import static org.junit.Assert.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.cache.ModisTileCache;
import edu.sdstate.eastweb.prototype.tests.*;

public class ModisTileCacheTests extends MetadataTests {
    @Test
    public void test() throws IOException {
        for (int i = 0; i < 100; ++i) {
            final File tempFile = File.createTempFile("test", null);
            try {
                final DataDate lastUpdated = DataDateTests.random();
                final Map<ModisTile, DataDate> tiles = new HashMap<ModisTile, DataDate>();
                for (int j = 0; j < 10; ++j) {
                    tiles.put(ModisTileTests.random(), DataDateTests.random());
                }
                final ModisTileCache ref = new ModisTileCache(lastUpdated, tiles);

                assertEquals(lastUpdated, ref.getLastUpdated());
                assertEquals(tiles, ref.getTiles());

                ref.toFile(tempFile);
                assertEquals(ref, ModisTileCache.fromFile(tempFile));
            } finally {
                FileUtils.deleteQuietly(tempFile);
            }
        }
    }
}