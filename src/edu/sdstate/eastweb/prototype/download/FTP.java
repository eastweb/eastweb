package edu.sdstate.eastweb.prototype.download;
import java.io.IOException;
import java.net.ConnectException;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class FTP extends ConnectionStrategy{

    @Override
    public  Object buildConn(ConnectionInfo ci) throws IOException {
        Object ftp=null;
        FTPConnectionInfo fci=(FTPConnectionInfo)ci;

        ftp = FtpClientPool.getFtpClient(fci.hostName, fci.userName, fci.password);
        return ftp;

    }

}