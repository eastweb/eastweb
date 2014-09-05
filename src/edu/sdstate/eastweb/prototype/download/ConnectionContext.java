package edu.sdstate.eastweb.prototype.download;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

public class ConnectionContext {

    static Object getConnection(Mode mode, DataType dt) throws ConnectException{
        Object connection=null;
        String modeStr=mode.toString();
        System.out.println("enter the get connection");
        try {
            //create connectionInfo object according to mode type.

            String classnameInfo="edu.sdstate.eastweb.prototype.download."+modeStr+"ConnectionInfo";
            System.out.println(classnameInfo);
            Class clsInfo = Class.forName(classnameInfo);
            Constructor ctor=clsInfo.getDeclaredConstructor(DataType.class);
            ctor.setAccessible(true);
            ConnectionInfo ci=(ConnectionInfo)ctor.newInstance(dt);

            //build and return connection
            String classnameStg="edu.sdstate.eastweb.prototype.download."+modeStr;
            System.out.println(classnameStg);
            Class clsStg=Class.forName(classnameStg);
            System.out.println(classnameStg);
            Class[] paramDatadate=new Class[1];
            paramDatadate[0]=Class.forName("edu.sdstate.eastweb.prototype.download.ConnectionInfo");
            Object obj=clsStg.newInstance();
            Method method=clsStg.getDeclaredMethod("buildConn", paramDatadate);
            connection=method.invoke(obj, ci);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //System.out.printlnc(clsStg);

            e.printStackTrace();
        }

        return connection;

    }

    static Object getConnection(Mode mode, DataType dt, Object product)throws ConnectException, ConfigReadException,IOException{
        ConnectionInfo ci=null;
        switch(mode){
        case FTP:
            ci=new FTPConnectionInfo(dt);
            return new FTP().buildConn(ci);
        case HTTP:
            if((dt==DataType.MODIS)){
                if((ModisProduct)product==ModisProduct.LST){
                    ci=new HTTPConnectionInfo(Config.getInstance().getModisLstUrl());
                }else{
                    ci=new HTTPConnectionInfo(Config.getInstance().getModisNbarUrl());
                }

            }

            return new HTTP().buildConn(ci);
        default:
            throw new IllegalArgumentException("Mode " + mode + " not supported.");

        }

    }



    static void close(Object conn){

    }

}
