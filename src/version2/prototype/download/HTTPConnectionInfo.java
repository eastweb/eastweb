package version2.prototype.download;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import version2.prototype.ConfigReadException;
import version2.prototype.PluginMetaDataCollection.DownloadMetaData;
import version2.prototype.download.Downloader.DataType;

public class HTTPConnectionInfo extends ConnectionInfo{
    String url;

    HTTPConnectionInfo(DataType dt, DownloadMetaData metadata) throws ParserConfigurationException, SAXException, IOException {
        mode="HTTP";
        url=metadata.myHttp.url;
    }

    HTTPConnectionInfo(String url) throws ConfigReadException {
        mode="HTTP";
        this.url=url;
    }
}
