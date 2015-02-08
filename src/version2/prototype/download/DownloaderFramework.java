package version2.prototype.download;

import java.io.IOException;
import org.xml.sax.SAXException;

public abstract class DownloaderFramework {

    public abstract void download() throws IOException, DownloadFailedException, Exception, SAXException;

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
