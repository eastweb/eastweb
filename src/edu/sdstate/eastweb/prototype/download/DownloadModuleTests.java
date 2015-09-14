package edu.sdstate.eastweb.prototype.download;

import java.io.*;
import java.util.*;
import edu.sdstate.eastweb.prototype.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * A set of tests for the download module.
 * Note: These generate quite a bit of network traffic.
 * 
 * @author Michael VanBemmel
 */
public class DownloadModuleTests {

    @Test
    public void testModisNbar() throws IOException, ConfigReadException {
        final List<DataDate> dates = ModisNbarDownloader.listDates(new DataDate(20, 6, 2010));
        final DataDate date = dates.get(dates.size() / 2); // Pick the middle date

        final Map<ModisTile, DataDate> tiles = ModisNbarDownloader.listTiles(date);
        final ModisTile tile = tiles.keySet().iterator().next();
        final DataDate processed = tiles.get(tile);
        final ModisId modisId = new ModisId(ModisProduct.NBAR, date, tile, processed);

        ModisDownloader.newWithProduct(modisId).download();

    }

    @Test
    public void testModisLst() throws IOException, ConfigReadException {
        final List<DataDate> dates = ModisLstDownloader.listDates(new DataDate(20, 6, 2010));
        final DataDate date = dates.get(dates.size() / 2); // Pick the middle date

        final Map<ModisTile, DataDate> tiles = ModisLstDownloader.listTiles(date);
        System.out.println(date);
        final ModisTile tile = tiles.keySet().iterator().next();
        final DataDate processed = tiles.get(tile);
        final ModisId modisId = new ModisId(ModisProduct.NBAR, date, tile, processed);

        ModisDownloader.newWithProduct(modisId).download();
    }

    /*@Test
      public void testTrmm() throws IOException {
        final List<DataDate> dates = TrmmDownloaderOld.listDates(new DataDate(20, 6, 2010));
        final DataDate date = dates.get(dates.size() / 2); // Pick the middle date

        final File file = File.createTempFile("test", null);
        try {
            new TrmmDownloaderOld(date, file).download();
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }
     */

    @Test
    public void testEto() throws IOException, ConfigReadException, DownloadFailedException {
        final List<EtoArchive> archives = EtoDownloader.listArchives(new DataDate(1, 1, 1990));
        final EtoArchive archive = archives.get(archives.size() / 2);
        new EtoDownloader(archive).download();
    }
}