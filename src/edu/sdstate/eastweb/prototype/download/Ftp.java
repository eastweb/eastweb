package edu.sdstate.eastweb.prototype.download;
import java.io.IOException;
import java.net.ConnectException;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class Ftp extends ConnectionStrategy{

    @Override
    public  Object buildConn(ConnectionInfo ci) throws IOException {
        Object ftp=null;
        if(ci.mode!=Mode.FTP){
            throw new IllegalArgumentException("Wrong connection infomation for Ftp");
        }else{
            ftp = FtpClientPool.getFtpClient(ci.hostName, ci.userName, ci.password);
            return ftp;
        }

    }

}
