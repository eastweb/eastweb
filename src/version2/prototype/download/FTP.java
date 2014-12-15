package version2.prototype.download;

import java.io.IOException;

public class FTP extends ConnectionStrategy{

    @Override
    public  Object buildConn(ConnectionInfo ci) throws IOException {
        FTPConnectionInfo fci=(FTPConnectionInfo)ci;

        return FtpClientPool.getFtpClient(fci.hostName, fci.userName, fci.password);
    }

}