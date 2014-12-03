package version2.prototype.download;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.xml.sax.SAXException;

import version2.prototype.Config;
import version2.prototype.ConfigReadException;
import version2.prototype.DataDate;
import version2.prototype.PluginMetaDataCollection.DownloadMetaData;


/**
 * This download class is automatically generated. Please go through all the
 * TODO tags before using it. Implements the NLDAS component of the download
 * module. Dependency: config.java; Downloader.Mode; Downloader.Settings;
 * Downloader.ConnectionContext
 */
public final class NldasDownloader extends Downloader {
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
    public static final List<DataDate> listDates(DataDate startDate)
            throws ConfigReadException, IOException, ParserConfigurationException, SAXException {
        List<DataDate> result=new ArrayList<DataDate>();
        try {

            //set up the parameters and method name.
            NldasDownloader nd=new NldasDownloader();
            Class cls=nd.getClass();
            Class[] paramDatadate=new Class[1];
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

    private static List<DataDate> ftpListDates(DataDate startDate) throws ParserConfigurationException, SAXException, IOException {

        final Pattern yearDirPattern = Pattern.compile("\\d{4}");
        // final Pattern dayOfYearDirPattern = Pattern.compile("\\d{3}");

        FTPClient ftp = null;

        try {
            ftp =
                    (FTPClient) ConnectionContext.getConnection(metaData.mode,
                            "NLDAS", metaData);
        } catch (ConnectException e) {
            System.out.println("Can't connect to NLDAS data website, please check your URL.");
            return null;
        }

        try {

            if (!ftp.changeWorkingDirectory(metaData.myFtp.rootDir)) {
                throw new IOException("Couldn't navigate to directory: "
                        + metaData.myFtp.rootDir);
            }

            // List years
            final List<DataDate> list = new ArrayList<DataDate>();
            for (FTPFile yearFile : ftp.listFiles()) {
                // Skip non-directory, non-year entries
                if (!yearFile.isDirectory()
                        || !yearDirPattern.matcher(yearFile.getName())
                        .matches()) {
                    continue;
                }

                final int year = Integer.parseInt(yearFile.getName());
                if (year < startDate.getYear()) {
                    continue;
                }

                // List days in this year
                // TODO: Please check the format of year dicrectory.
                final String yearDirectory =
                        String.format("%s/%s", metaData.myFtp.rootDir, yearFile.getName());

                if (!ftp.changeWorkingDirectory(yearDirectory)) {
                    throw new IOException(
                            "Couldn't navigate to directory: "
                                    + yearDirectory);
                }

                // The path of NLDS is \year\dayOfYear\fileName.grd
                for (FTPFile file : ftp.listDirectories()) {
                    int dayOfYear = Integer.parseInt(file.getName());
                    if (dayOfYear < startDate.getDayOfYear()) {
                        continue;
                    }
                    //move into day folder and count the number of hourly data
                    final String dayDirectory=String.format("%s/%03d", yearDirectory , dayOfYear);
                    System.out.println(dayDirectory);
                    if (!ftp.changeWorkingDirectory(dayDirectory)) {
                        throw new IOException(
                                "Couldn't navigate to directory: "
                                        + yearDirectory);
                    }
                    int hourlyFileNum=ftp.listFiles().length/2;
                    //Add hourly data into list, start with 0 to 23
                    for(int i=0;i<hourlyFileNum;i++){
                        final DataDate dataDate =
                                DataDate.DataDateWithHour(i, dayOfYear,
                                        Integer.parseInt(yearFile.getName()));
                        if (dataDate.compareTo(startDate) >= 0) {
                            System.out.println(dataDate.toCompactString());
                            list.add(dataDate);
                        }
                    }
                    //move back to year folder
                    ftp.changeWorkingDirectory(yearDirectory);
                }

            }

            return list;
        } finally {
            FtpClientPool.returnFtpClient(Config.getInstance()
                    .getNLDASFtpHostName(), ftp);
        }

    }

    private static List<DataDate> httpListDates(DataDate startDate){
        return null;
    }

    @Override
    public final void download() throws IOException, ConfigReadException,
    DownloadFailedException, SAXException, Exception {
        String mode = metaData.mode;

        //set up the parameters and method name.
        NldasDownloader nd=new NldasDownloader();
        Class cls=nd.getClass();
        Class[] paramDatadate=new Class[2];
        paramDatadate[0]=mDate.getClass();
        paramDatadate[1]=Class.forName("java.io.File");
        String methodName=mode.toLowerCase()+"Download";
        //call the listDates method for different download protocol
        Method method=cls.getDeclaredMethod(methodName,null);
        Constructor ctor=cls.getDeclaredConstructor(paramDatadate);
        ctor.setAccessible(true);
        Object obj=ctor.newInstance(mDate,mOutFile);
        method.invoke(obj, null);

    }

    private void ftpDownload() throws ParserConfigurationException, SAXException, IOException {
        String mode = metaData.mode;
        String rootDir=metaData.myFtp.rootDir;
        final FTPClient ftp =
                (FTPClient) ConnectionContext.getConnection(mode,
                        "NLDAS", metaData);
        try {
            // TODO: Change the year directory as needed.
            final String yearDirectory =
                    String.format("%s/%s",
                            //Settings.getRootDir(DataType.NLDAS),
                            rootDir,
                            Integer.toString(mDate.getYear()));

            if (!ftp.changeWorkingDirectory(yearDirectory)) {
                throw new IOException("Couldn't navigate to directory: "
                        + yearDirectory);

            }

            // Change to the day of year directory. Added by Jiameng Hu
            int dayOfYear = mDate.getDayOfYear();
            final String dayDirectory =
                    String.format("%s/%03d", yearDirectory, dayOfYear);
            if (!ftp.changeWorkingDirectory(dayDirectory)) {
                throw new IOException("Couldn't navigate to directory: "
                        + dayDirectory);
            }

            // TODO: Change the string format as needed. Add hour into date
            // information by Jiameng
            String targetFile =
                    String.format(
                            "NLDAS_FORA0125_H.A%04d%02d%02d.%04d.002.grb",
                            mDate.getYear(), mDate.getMonth(),
                            mDate.getDay(),
                            // The format of hour of 2 is 0200. by Jiameng
                            mDate.getHour() * 100);

            DownloadUtils.download(ftp, targetFile, mOutFile);
        } catch (IOException e) { // FIXME: ugly fix so that the system
            // doesn't repeatedly try and fail
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            FtpClientPool.returnFtpClient(Config.getInstance()
                    .getNLDASFtpHostName(), ftp);
        }
    }

    private void httpDownload(){

    }

}