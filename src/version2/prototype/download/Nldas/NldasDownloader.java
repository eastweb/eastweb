package version2.prototype.download.Nldas;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import version2.prototype.ConfigReadException;
import version2.prototype.DataDate;
import version2.prototype.PluginMetaData.PluginMetaDataCollection.DownloadMetaData;
import version2.prototype.download.DownloadFailedException;
import version2.prototype.download.DownloaderFramework;

/**
 * This download class is automatically generated. Please go through all the
 * TODO tags before using it. Implements the NLDAS component of the download
 * module. Dependency: config.java; Downloader.Mode; Downloader.Settings;
 * Downloader.ConnectionContext
 */
public final class NldasDownloader extends DownloaderFramework {
    private final DataDate mDate;
    private final File mOutFile;

    private static DownloadMetaData metaData;

    public NldasDownloader(DataDate date, File outFile, String NldasMode, String NldasRootDir) throws IOException {
        mDate = date;
        mOutFile = outFile;
    }

    public NldasDownloader(DataDate date, File outFile, DownloadMetaData data) {
        mDate=date;
        mOutFile=outFile;
        metaData = data;
    }

    public NldasDownloader() {
        mDate=null;
        mOutFile=null;
    }

    /**
     * Builds and returns a list containing all of the available data dates no
     * earlier than the specified start date.
     *
     * @param startDate
     *            Specifies the inclusive lower bound for the returned data date
     *            list
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @SuppressWarnings("unchecked")
    public static final List<DataDate> listDates(DataDate startDate)
            throws ConfigReadException, IOException, ParserConfigurationException, SAXException {

        List<DataDate> result=new ArrayList<DataDate>();
        try {

            //set up the parameters and method name.
            NldasDownloader nd=new NldasDownloader();
            Class<?> cls=nd.getClass();
            Class<?>[] paramDatadate=new Class[1];
            paramDatadate[0]=startDate.getClass();
            String methodName=metaData.mode.toLowerCase()+"ListDates";


            //call the listDates method for different download protocol
            Method method=cls.getDeclaredMethod(methodName,paramDatadate);
            result=(List<DataDate>) method.invoke(null, startDate);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public final void download() throws IOException, ConfigReadException,
    DownloadFailedException, SAXException, Exception {
        String mode = metaData.mode;

        //set up the parameters and method name.
        NldasDownloader nd=new NldasDownloader();
        Class<?> cls=nd.getClass();
        Class<?>[] paramDatadate=new Class[2];
        paramDatadate[0]=mDate.getClass();
        paramDatadate[1]=Class.forName("java.io.File");
        String methodName=mode.toLowerCase()+"Download";

        //call the listDates method for different download protocol
        Method method=cls.getDeclaredMethod(methodName);
        Constructor<?> ctor=cls.getDeclaredConstructor(paramDatadate);
        ctor.setAccessible(true);
        Object obj=ctor.newInstance(mDate,mOutFile);
        method.invoke(obj);
    }
}