package edu.sdstate.eastweb.prototype.download;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Http extends ConnectionStrategy{

    @Override
    public Object buildConn(ConnectionInfo ci) throws IOException {
        final URL url = new URL(ci.url);
        //System.out.println(ci.url);
        URLConnection conn = url.openConnection();
        return conn;
    }

}
