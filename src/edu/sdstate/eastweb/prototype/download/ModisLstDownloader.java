package edu.sdstate.eastweb.prototype.download;

import java.io.IOException;
import java.net.URLConnection;
import java.util.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;


/**
 * Implements the MODIS LST component of the download module.
 * 
 * @author Dan Woodward
 * @author Michael VanBemmel
 */
public final class ModisLstDownloader extends ModisDownloader {
    //private static final String ROOT_DIRECTORY = "/MOLT/MOD11A2.005";
    private static final String PRODUCT = "MOD11A2";

    /**
     * Constructs a new ModisLstDownloader.
     */
    public ModisLstDownloader(DataDate date, ModisTile tile, DataDate processed)
    throws ConfigReadException
    {
        super(
                date, tile, processed,
                DirectoryLayout.getModisDownload(ModisProduct.LST, date, tile)
        );
    }

    public static final List<DataDate> listDates(DataDate startDate) throws IOException {
        Mode mode=Settings.getMode(DataType.MODIS);
        Object conn=ConnectionContext.getConnection(mode, DataType.MODIS, ModisProduct.LST);
        return listDates(startDate, conn);
    }

    public static final Map<ModisTile, DataDate> listTiles(DataDate date) throws IOException {

        return listTiles(date, Config.getInstance().getModisLstUrl(), PRODUCT);
    }

    @Override
    protected String getRootDir() {
        // TODO: handle better
        try {
            return Config.getInstance().getModisLstUrl();
        } catch (ConfigReadException e) {
            return "";
        }
    }

    @Override
    protected String getProduct() {
        return PRODUCT;
    }
}
