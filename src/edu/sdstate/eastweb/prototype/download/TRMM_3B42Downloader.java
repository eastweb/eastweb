package edu.sdstate.eastweb.prototype.download;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.lang.reflect.*;

import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;

/**
 *This download class is automatically generated. Please go through all the TODO tags before using it.
 *Implements the TRMM_3B42 component of the download module.
 *Dependency: config.java; Downloader.Mode; Downloader.Settings; Downloader.ConnectionContext
 */
public final class TRMM_3B42Downloader extends Downloader {
    private final DataDate mDate;
    private final File mOutFile;
    private static Mode mode;

    public TRMM_3B42Downloader(DataDate date, File outFile) throws IOException {
        mDate = date;
        mOutFile = outFile;

    }

    public TRMM_3B42Downloader() {
        mDate=null;
        mOutFile=null;
        mode=null;

    }

    /**
     * Builds and returns a list containing all of the available data dates no earlier than the specified
     * start date. If fail, throw exception and return an empty list.
     * @param startDate Specifies the inclusive lower bound for the returned data date list
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static final List<DataDate> listDates(DataDate startDate) throws ConfigReadException, IOException {
        mode=Settings.getMode(DataType.TRMM_3B42);
        List<DataDate> result=new ArrayList<DataDate>();
        try {

            //set up the parameters and method name.
            TRMM_3B42Downloader td=new TRMM_3B42Downloader();
            Class cls=td.getClass();
            Class[] paramDatadate=new Class[1];
            paramDatadate[0]=startDate.getClass();
            String methodName=mode.toString().toLowerCase()+"ListDates";

            //call the listDates method for different download protocol
            Method method=cls.getDeclaredMethod(methodName,paramDatadate);
            result=(List<DataDate>) method.invoke(null, startDate);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void download() throws IOException, ConfigReadException, DownloadFailedException {
        System.out.println("baby");
        //mode=Settings.getMode(DataType.TRMM_3B42);
        mode=Mode.FTP;
        String ss=mode.toString();
        System.out.println(ss);
        try {

            //set up the parameters and method name.
            TRMM_3B42Downloader td=new TRMM_3B42Downloader();
            Class cls=td.getClass();
            Class[] paramDatadate=new Class[2];
            paramDatadate[0]=mDate.getClass();
            paramDatadate[1]=Class.forName("java.io.File");
            //String s1=mode.toString();
            String s2=ss.toLowerCase();
            String s3=s2+"Download";
            // String methodName=mode.toString().toLowerCase()+"Download";
            String methodName=s3;
            System.out.println(s3);
            //call the listDates method for different download protocol
            Method method=cls.getDeclaredMethod(methodName,null);
            Constructor ctor=cls.getDeclaredConstructor(paramDatadate);
            ctor.setAccessible(true);
            Object obj=ctor.newInstance(mDate,mOutFile);
            method.invoke(obj, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void httpDowload() throws IOException{
        //TODO: Create url based on date
        String url_str=Config.getInstance().getTRMM_3B42Url()+String.format(
                "%04d.%02d.%02d",mDate.getYear(),mDate.getMonth(),mDate.getDay());
        // Download the archive
        URL url = new URL(url_str);
        try{
            DownloadUtils.downloadToFile(url, mOutFile);
        }catch (IOException e) { // FIXME: ugly fix so that the system doesn't repeatedly try and fail
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
    }

    private void ftpDownload() throws ConnectException, ConfigReadException, IOException{
        mode=Settings.getMode(DataType.TRMM_3B42);
        final FTPClient ftp = (FTPClient) ConnectionContext.getConnection(mode, DataType.TRMM_3B42);
        try {
            //TODO: Change the year directory as needed.
            final String yearDirectory = String.format(
                    "%s/%s",
                    Settings.getRootDir(DataType.TRMM_3B42),
                    Integer.toString(mDate.getYear())
                    );
            if (!ftp.changeWorkingDirectory(yearDirectory)) {
                throw new IOException("Couldn't navigate to directory: " + yearDirectory);
            }
            //TODO: Change the string format as needed.
            String targetFile=String.format(
                    "3B42_daily.%04d.%02d.%02d.7.bin",
                    mDate.getYear(),
                    mDate.getMonth(),
                    mDate.getDay()
                    );
            DownloadUtils.download(ftp, targetFile, mOutFile);
        } catch (IOException e) { // FIXME: ugly fix so that the system doesn't repeatedly try and fail
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            throw e;
        } finally {
            FtpClientPool.returnFtpClient(Config.getInstance().getTRMM_3B42FtpHostName(), ftp);
        }
    }

    private static List<DataDate> ftpListDates(DataDate startDate) throws IOException{
        //TODO: Please check the patterns to make sure that they are compatible.
        final Pattern yearDirPattern = Pattern.compile("\\d{4}");
        final Pattern dayOfYearDirPattern = Pattern.compile("\\d{3}");

        FTPClient ftp=null;
        String rootDir=Config.getInstance().getTRMM_3B42RootDir();

        try{
            ftp = (FTPClient) ConnectionContext.getConnection(mode, DataType.TRMM_3B42);
        }catch(ConnectException e){
            System.out.println("Can't connect to TRMM_3B42 data website, please check your URL.");
            return null;
        }

        try {

            if (!ftp.changeWorkingDirectory(rootDir)) {
                throw new IOException("Couldn't navigate to directory: " + rootDir);
            }

            // List years
            final List<DataDate> list = new ArrayList<DataDate>();
            for (FTPFile yearFile : ftp.listFiles()) {
                // Skip non-directory, non-year entries
                if (!yearFile.isDirectory() ||
                        !yearDirPattern.matcher(yearFile.getName()).matches()) {
                    continue;
                }

                final int year = Integer.parseInt(yearFile.getName());
                if (year < startDate.getYear()) {
                    continue;
                }

                // List days in this year
                //TODO: Please check the format of year dicrectory.
                final String yearDirectory = String.format("%s/%s", rootDir, yearFile.getName());

                if (!ftp.changeWorkingDirectory(yearDirectory)) {
                    throw new IOException("Couldn't navigate to directory: " + yearDirectory);
                }

                //TODO: Create your pattern here
                Pattern mpattern=Pattern.compile("3B42_daily\\.(\\d{4})\\.(\\d{2})\\.(\\d{2})\\.7\\.bin");

                for (FTPFile file : ftp.listFiles()) {
                    if (file.isFile() && mpattern.matcher(file.getName()).matches()) {
                        // TODO: Assume following formatis {product name}.%y4.%m2.%d2.7.bin, please change it as needed
                        String[] strings = file.getName().split("[.]");
                        final int month = Integer.parseInt(strings[2]);
                        final int day = Integer.parseInt(strings[3]);

                        final DataDate dataDate = new DataDate(day, month, year);
                        if (dataDate.compareTo(startDate) >= 0) {
                            list.add(dataDate);
                        }
                    }
                }
            }

            return list;
        } finally {
            FtpClientPool.returnFtpClient(Config.getInstance().getTRMM_3B42FtpHostName(), ftp);
        }

    }

    private static List<DataDate> httpListDates(DataDate startDate) throws ConnectException, ConfigReadException, IOException{
        //create url connection
        URLConnection conn=(URLConnection)ConnectionContext.getConnection(mode, DataType.TRMM_3B42);
        //TODO: obtain the date list of available data
        final List<DataDate> list = new ArrayList<DataDate>();
        return list;


    }
}
