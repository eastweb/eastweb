package edu.sdstate.eastweb.prototype.download;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.MetaData;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class HTTPConnectionInfo extends ConnectionInfo{
    String url;
    HTTPConnectionInfo(DataType dt) throws ParserConfigurationException, SAXException, IOException {
        mode="HTTP";
        url=MetaData.GetInstance().get(dt).Download.myHttp.url;

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
