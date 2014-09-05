package edu.sdstate.eastweb.prototype.download;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class FTPConnectionInfo extends ConnectionInfo {

    String hostName;
    String userName;
    String password;

    FTPConnectionInfo(DataType dt) throws ConfigReadException{
        mode=Mode.FTP;
        hostName=Settings.getHostName(dt);
        userName=Settings.getUserName(dt);
        password=Settings.getPassword(dt);
    }
    /*FTPConnectionInfo(){
        super();
        mode=Mode.FTP;
    }*/


}
