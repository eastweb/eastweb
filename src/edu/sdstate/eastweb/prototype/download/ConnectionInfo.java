package edu.sdstate.eastweb.prototype.download;

import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class ConnectionInfo {
    Mode mode;
    String hostName;
    String userName;
    String password;
    String url;

    ConnectionInfo(Mode m, String hn, String un, String pw){
        mode=m;
        hostName=hn;
        userName=un;
        password=pw;
        url="";
    }

    ConnectionInfo(Mode m, String url){
        mode=m;
        hostName="";
        userName="";
        password="";
        this.url=url;
    }

}
