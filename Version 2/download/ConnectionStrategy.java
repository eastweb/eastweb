package edu.sdstate.eastweb.prototype.download;
import java.io.IOException;

public abstract class ConnectionStrategy {

    public abstract Object buildConn(ConnectionInfo ci) throws IOException;

}
