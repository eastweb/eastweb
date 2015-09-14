package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.download.cache.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.CallableTask;

public class UpdateModisTileCacheTask implements CallableTask<ModisTileCache> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final ModisProduct mProduct;
    private final DataDate mDate;

    public UpdateModisTileCacheTask(ModisProduct product, DataDate date) {
        mProduct = product;
        mDate = date;
    }

    private File getFile() throws ConfigReadException {
        return DirectoryLayout.getModisTileCache(mProduct, mDate);
    }

    @Override
    public ModisTileCache getCanSkip() {
        try {
            final ModisTileCache cache = ModisTileCache.fromFile(getFile());

            // Check freshness
            if (!CacheUtils.isFresh(cache)) {
                return null;
            }

            return cache;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public ModisTileCache call() throws IOException {
        // Get a list of tiles and construct a tile cache
        final ModisTileCache cache = new ModisTileCache(
                DataDate.today(),
                ModisDownloader.listTiles(mProduct, mDate)
        );

        // Write out the tile cache
        final File file = getFile();
        FileUtils.forceMkdir(file.getParentFile());
        cache.toFile(file);

        return cache;
    }

    @Override
    public String getName() {
        return String.format(
                "Update MODIS tile cache: product=%s, date=%s",
                mProduct,
                mDate.toCompactString()
        );
    }

}