package edu.sdstate.eastweb.prototype.download;
import java.io.IOException;
public abstract class Downloader {

    public abstract void download() throws IOException, DownloadFailedException;
    public enum DataType {
        TRMM,
        ETO,
        MODIS,
        TRMM_3B42,
        NLDAS
    }

    public enum Mode{
        HTTP,
        FTP
    }

}
