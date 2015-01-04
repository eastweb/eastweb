package version2.prototype.download;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.ConnectException;

import version2.prototype.PluginMetaDataCollection.DownloadMetaData;

public class ConnectionContext {

    static Object getConnection(String mode, String dt, DownloadMetaData metadata) throws ConnectException{
        Object connection=null;

        try {

            String classnameInfo="version2.prototype.download."+mode+"ConnectionInfo";
            Class<?> clsInfo = Class.forName(classnameInfo);

            String classnameStg="version2.prototype.download."+mode;
            Class<?> clsStg=Class.forName(classnameStg);
            Class<?>[] paramDatadate=new Class[1];

            //create connectionInfo object according to mode type.
            Constructor<?> ctor= clsInfo.getDeclaredConstructor(String.class, DownloadMetaData.class);
            ctor.setAccessible(true);
            ConnectionInfo ci=(ConnectionInfo)ctor.newInstance(dt, metadata);

            //build and return connection
            paramDatadate[0]=Class.forName("version2.prototype.download.ConnectionInfo");
            Object obj=clsStg.newInstance();
            Method method=clsStg.getDeclaredMethod("buildConn", paramDatadate);
            connection=method.invoke(obj, ci);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    static void close(Object conn){

    }
}
