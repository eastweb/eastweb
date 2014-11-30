package version2.prototype.download;

import java.io.IOException;


public class FTP extends ConnectionStrategy{

    @Override
    public  Object buildConn(ConnectionInfo ci) throws IOException {
        Object ftp=null;
        FTPConnectionInfo fci=(FTPConnectionInfo)ci;

        ftp = FtpClientPool.getFtpClient(fci.hostName, fci.userName, fci.password);
        return ftp;

    }

}