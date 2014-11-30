package version2.prototype.download;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import version2.prototype.ConfigReadException;
import version2.prototype.download.Downloader.DataType;


public class HTTPConnectionInfo extends ConnectionInfo{
    String url;
    HTTPConnectionInfo(DataType dt) throws ParserConfigurationException, SAXException, IOException {
        mode="HTTP";
        //url=MetaData.GetInstance().get(dt).Download.myHttp.url;

    }

    HTTPConnectionInfo(String url) throws ConfigReadException {
        mode="HTTP";
        this.url=url;

    }

    /*HTTPConnectionInfo(){
        super();
        mode=Mode.HTTP;

    }*/
}
