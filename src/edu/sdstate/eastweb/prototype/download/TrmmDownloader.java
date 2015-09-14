package edu.sdstate.eastweb.prototype.download;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;


public class TrmmDownloader extends Downloader{

    private final TrmmProduct mProduct;
    private final DataDate mDate;
    private final File mOutFile;


    public TrmmDownloader(TrmmProduct product, DataDate date, File outFile) throws IOException {
        mProduct = product;
        mDate = date;
        mOutFile = outFile;


    }

    /**
     * Builds and returns a list containing all of the available data dates no earlier than the specified
     * start date.
     * @param startDate Specifies the inclusive lower bound for the returned data date list
     * @throws IOException
     */
    public static final List<DataDate> listDates(TrmmProduct product, DataDate startDate) throws ConfigReadException, IOException {
        Mode mode=Settings.getMode(DataType.TRMM);
        if(mode==Mode.FTP){
            final Pattern yearDirPattern = Pattern.compile("\\d{4}");
            final Pattern dayOfYearDirPattern = Pattern.compile("\\d{3}");
            FTPClient ftp=null;

            try{
                ftp = (FTPClient) ConnectionContext.getConnection(mode, DataType.TRMM);
            }catch(ConnectException e){
                System.out.println("Can't connect to Trmm data website, please check your URL.");
                return null;
            }

            try {
                if (!ftp.changeWorkingDirectory(Settings.getRootDir(product))) {
                    throw new IOException("Couldn't navigate to directory: " + Settings.getRootDir(product));
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
                    final String yearDirectory = String.format("%s/%s", Settings.getRootDir(product), yearFile.getName());
                    if (!ftp.changeWorkingDirectory(yearDirectory)) {
                        throw new IOException("Couldn't navigate to directory: " + yearDirectory);
                    }

                    for (FTPFile file : ftp.listFiles()) {
                        if (file.isFile() && Settings.getTrmmPattern(product).matcher(file.getName()).matches()) {
                            // Assume following format: {product name}.%y4.%m2.%d2.7.bin
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
                FtpClientPool.returnFtpClient(Config.getInstance().getTrmmFtpHostName(), ftp);
            }

        }else{
            //TODO: code for http connection
            return null;
        }

    }

    /**
     * Downloads a TRMM data set to the specified file.
     * @param date Data set date
     * @param outFile Destination file
     * @throws IOException
     */
    @Override
    public final void download() throws IOException {
        Mode mode=Settings.getMode(DataType.TRMM);
        if(mode==Mode.FTP){
            final FTPClient ftp = (FTPClient) ConnectionContext.getConnection(mode, DataType.TRMM);
            try {
                final String yearDirectory = String.format(
                        "%s/%04d",
                        Settings.getRootDir(mProduct),
                        mDate.getYear()
                        );
                if (!ftp.changeWorkingDirectory(yearDirectory)) {
                    throw new IOException("Couldn't navigate to directory: " + yearDirectory);
                }

                DownloadUtils.download(ftp, Settings.getFilename(mProduct, mDate), mOutFile);
            } catch (IOException e) { // FIXME: ugly fix so that the system doesn't repeatedly try and fail
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                throw e;
            } finally {
                FtpClientPool.returnFtpClient(Config.getInstance().getTrmmFtpHostName(), ftp);
            }
        }else{
            //mode is http

        }

    }







}
