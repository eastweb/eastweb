package edu.sdstate.eastweb.prototype.download;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class HTTPConnectionInfo extends ConnectionInfo{
    String url;
    HTTPConnectionInfo(DataType dt) throws ConfigReadException {
        mode=Mode.HTTP;
        url=Settings.getUrl(dt);

    }

    HTTPConnectionInfo(String url) throws ConfigReadException {
        mode=Mode.HTTP;
        this.url=url;

    }

    /*HTTPConnectionInfo(){
        super();
        mode=Mode.HTTP;

    }*/
}
