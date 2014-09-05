package edu.sdstate.eastweb.prototype.download;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class HTTP extends ConnectionStrategy{

    @Override
    public Object buildConn(ConnectionInfo ci) throws IOException {
        HTTPConnectionInfo hci=(HTTPConnectionInfo)ci;
        final URL url = new URL(hci.url);
        //System.out.println(ci.url);
        URLConnection conn = url.openConnection();
        return conn;
    }

}
