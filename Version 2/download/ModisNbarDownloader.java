package edu.sdstate.eastweb.prototype.download;

import java.io.IOException;
import java.util.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;


/**
 * Implements the MODIS NBAR component of the download module.
 * 
 * @author Dan Woodward
 * @author Michael VanBemmel
 */
public final class ModisNbarDownloader extends ModisDownloader {
    private static final String PRODUCT = "MCD43B4";

    /**
     * Constructs a new ModisNbarDownloader.
     */
    public ModisNbarDownloader(DataDate date, ModisTile tile, DataDate processed)
    throws ConfigReadException
    {
        super(
                date, tile, processed,
                DirectoryLayout.getModisDownload(ModisProduct.NBAR, date, tile)
        );
    }

    public static final List<DataDate> listDates(DataDate startDate) throws IOException {
        Mode mode=Settings.getMode(DataType.MODIS);
        Object conn=ConnectionContext.getConnection(mode, DataType.MODIS, ModisProduct.NBAR);
        return listDates(startDate,  conn);
    }

    public static final Map<ModisTile, DataDate> listTiles(DataDate date) throws IOException {
        return listTiles(date,  Config.getInstance().getModisNbarUrl(), PRODUCT);
    }

    @Override
    protected String getRootDir() {
        // TODO: handle better
        try {
            return Config.getInstance().getModisNbarUrl();
        } catch (ConfigReadException e) {
            return "";
        }
    }

    @Override
    protected String getProduct() {
        return PRODUCT;
    }
}
