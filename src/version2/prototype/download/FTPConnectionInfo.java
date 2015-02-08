package version2.prototype.download;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import version2.prototype.PluginMetaData.PluginMetaDataCollection.DownloadMetaData;


public class FTPConnectionInfo extends ConnectionInfo {

    String hostName;
    String userName;
    String password;

    FTPConnectionInfo(String dt, DownloadMetaData metadata) throws ParserConfigurationException, SAXException, IOException{
        mode="FTP";
        hostName=metadata.myFtp.hostName;
        userName=metadata.myFtp.userName;
        password=metadata.myFtp.password;
    }
}
