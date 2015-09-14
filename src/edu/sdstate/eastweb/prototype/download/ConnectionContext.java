package edu.sdstate.eastweb.prototype.download;
import java.io.IOException;
import java.net.ConnectException;
import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class ConnectionContext {
    static Object getConnection(Mode mode, DataType dt) throws ConnectException, ConfigReadException,IOException{

        ConnectionInfo ci=null;
        switch(mode){
        case FTP:
            ci=new ConnectionInfo(mode,Settings.getHostName(dt),Settings.getUserName(dt),Settings.getPassword(dt));
            return new Ftp().buildConn(ci);
        case HTTP:
            if((dt==DataType.MODIS)){
                throw new IllegalArgumentException("Need product information for Mode " + mode);
            }
            ci=new ConnectionInfo(mode,Settings.getUrl(dt));
            //System.out.println("hello"+Settings.getUrl(dt));
            return new Http().buildConn(ci);
        default:
            throw new IllegalArgumentException("Mode " + mode + " not supported.");

        }

    }

    static Object getConnection(Mode mode, DataType dt, Object product)throws ConnectException, ConfigReadException,IOException{
        ConnectionInfo ci=null;
        switch(mode){
        case FTP:
            ci=new ConnectionInfo(mode,Settings.getHostName(dt),Settings.getUserName(dt),Settings.getPassword(dt));
            return new Ftp().buildConn(ci);
        case HTTP:
            if((dt==DataType.MODIS)){
                if((ModisProduct)product==ModisProduct.LST){
                    ci=new ConnectionInfo(mode,Config.getInstance().getModisLstUrl());
                }else{
                    ci=new ConnectionInfo(mode,Config.getInstance().getModisNbarUrl());
                }

            }

            return new Http().buildConn(ci);
        default:
            throw new IllegalArgumentException("Mode " + mode + " not supported.");

        }
    }

    static void close(Object conn){

    }

}
