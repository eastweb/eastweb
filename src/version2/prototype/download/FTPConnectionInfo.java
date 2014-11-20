package version2.prototype.download;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.MetaData;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class FTPConnectionInfo extends ConnectionInfo {

    String hostName;
    String userName;
    String password;

    FTPConnectionInfo(String dt) throws ParserConfigurationException, SAXException, IOException{
        mode="FTP";
        hostName=MetaData.GetInstance().get(dt).Download.myFtp.hostName;
        userName=MetaData.GetInstance().get(dt).Download.myFtp.userName;
        password=MetaData.GetInstance().get(dt).Download.myFtp.password;
    }
    /*FTPConnectionInfo(){
        super();
        mode=Mode.FTP;
    }*/


}
