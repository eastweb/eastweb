package edu.sdstate.eastweb.prototype.download;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import edu.sdstate.eastweb.prototype.*;

/**
 *This download class is automatically generated. Please go through all the TODO tags before using it.
 *Implements the NLDAS component of the download module.
 *Dependency: config.java; Downloader.Mode; Downloader.Settings; Downloader.ConnectionContext
 */
public final class NLDASDownloader extends Downloader {
    private final DataDate mDate;
    private final File mOutFile;

    public NLDASDownloader(DataDate date, File outFile) throws IOException {
        mDate = date;
        mOutFile = outFile;
    }

    /**
     * Builds and returns a list containing all of the available data dates no earlier than the specified
     * start date.
     * @param startDate Specifies the inclusive lower bound for the returned data date list
     * @throws IOException
     */
    public static final List<DataDate> listDates(DataDate startDate) throws ConfigReadException, IOException {
        Mode mode=Settings.getMode(DataType.NLDAS);
        if(mode==Mode.FTP){
            //TODO: Please check the patterns to make sure that they are compatible.
            final Pattern yearDirPattern = Pattern.compile("\\d{4}");
            final Pattern dayOfYearDirPattern = Pattern.compile("\\d{3}");

            FTPClient ftp=null;
            String rootDir=Config.getInstance().getNLDASRootDir();

            try{
                ftp = (FTPClient) ConnectionContext.getConnection(mode, DataType.NLDAS);
            }catch(ConnectException e){
                System.out.println("Can't connect to NLDAS data website, please check your URL.");
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

                    //The path of NLDS is \year\dayOfYear\fileName.grd
                    for (FTPFile file : ftp.listDirectories()) {
                        int dayOfYear=Integer.parseInt(file.getName());
                        final DataDate dataDate=new DataDate(dayOfYear,Integer.parseInt(yearFile.getName()));
                        if (dataDate.compareTo(startDate) >= 0) {
                            list.add(dataDate);
                        }
                    }



                    /* NLDS file have different file structure. list days need be rewrite.
                     *
                    //TODO: Create your pattern here
                    Pattern mpattern=Pattern.compile("NLDAS_FORA0125_H\\.A(\\d{4})(\\d{2})(\\d{2})\\.(\\d{4})\\.002\\.grb");

                    for (FTPFile file : ftp.listFiles()) {
                        if (file.isFile() && mpattern.matcher(file.getName()).matches()) {
                            // TODO: Assume following format is {product name}.%y4.%m2.%d2.7.bin, please change it as needed
                            String[] strings = file.getName().split("[.]");
                            final int month = Integer.parseInt(strings[2]);
                            final int day = Integer.parseInt(strings[3]);

                            final DataDate dataDate = new DataDate(day, month, year);
                            if (dataDate.compareTo(startDate) >= 0) {
                                list.add(dataDate);
                            }
                        }
                    }*/

                }

                return list;
            } finally {
                FtpClientPool.returnFtpClient(Config.getInstance().getNLDASFtpHostName(), ftp);
            }

        }else{
            //create url connection
            URLConnection conn=(URLConnection)ConnectionContext.getConnection(mode, DataType.NLDAS);
            //TODO: Please check the pattern to make sure that it is compatible.
            final Pattern re = Pattern.compile("(\\d{4})\\.(\\d{2})\\.(\\d{2})");
            byte[] downloadPage=null;
            try{
                downloadPage = DownloadUtils.downloadToByteArray(conn);
            }catch(ConnectException e){
                throw e;
            }


            // Parse it into a DOM tree
            final HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
            Document pagedoc = null;
            try {
                pagedoc = builder.parse(new ByteArrayInputStream(downloadPage));
            } catch (SAXException e) {
                throw new IOException("Failed to parse the NLDAS download page", e);
            }

            final NodeList dirlist = pagedoc.getElementsByTagName("a");

            final List<DataDate> list = new ArrayList<DataDate>();

            for (int i = 0; i < dirlist.getLength(); ++i) {
                final String dir = ((Element)dirlist.item(i)).getAttribute("href");

                // Match the filename against the pattern
                final Matcher matcher = re.matcher(dir);
                if (matcher.matches()) {
                    //TODO: Please change this part if you have different pattern string
                    final int year = Integer.parseInt(matcher.group(1));
                    final int month = Integer.parseInt(matcher.group(2));
                    final int day = Integer.parseInt(matcher.group(3));

                    final DataDate dataDate = new DataDate(day, month, year);
                    if (startDate == null || dataDate.compareTo(startDate) >= 0) {
                        list.add(dataDate);
                    }
                }
            }

            return list;

        }

    }

    @Override
    public final void download() throws IOException, ConfigReadException, DownloadFailedException {
        Mode mode=Settings.getMode(DataType.NLDAS);
        if(mode==Mode.FTP){
            final FTPClient ftp = (FTPClient) ConnectionContext.getConnection(mode, DataType.NLDAS);
            try {
                //TODO: Change the year directory as needed.
                final String yearDirectory = String.format(
                        "%s/%s",
                        Settings.getRootDir(DataType.NLDAS),
                        Integer.toString(mDate.getYear())
                );


                if (!ftp.changeWorkingDirectory(yearDirectory)) {
                    throw new IOException("Couldn't navigate to directory: " + yearDirectory);

                }

                //Change to the day of year directory. Added by Jiameng Hu
                int dayOfYear=mDate.getDayOfYear();
                final String dayDirectory=String.format("%s/%3d",yearDirectory, dayOfYear);
                if (!ftp.changeWorkingDirectory(dayDirectory)) {
                    throw new IOException("Couldn't navigate to directory: " + dayDirectory);
                }

                //TODO: Change the string format as needed. Add hour into date information by Jiameng
                String targetFile=String.format(
                        "NLDAS_FORA0125_H.A%04d%02d%02d.%04d.002.grb",
                        mDate.getYear(),
                        mDate.getMonth(),
                        mDate.getDay(),
                        //The format of hour of 2 is 0200. by Jiameng
                        mDate.getHour()*100
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
                FtpClientPool.returnFtpClient(Config.getInstance().getNLDASFtpHostName(), ftp);
            }
        }else{
            //mode is http
            //TODO: Create url based on date
            String url_str=Config.getInstance().getNLDASUrl()+String.format(
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
    }

}
