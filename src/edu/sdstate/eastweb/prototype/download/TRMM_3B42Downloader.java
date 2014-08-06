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
import edu.sdstate.eastweb.prototype.*;

/**
 * This download class is automatically generated. Please go through all the
 * TODO tags before using it. Implements the TRMM_3B42 component of the download
 * module. Dependency: config.java; Downloader.Mode; Downloader.Settings;
 * Downloader.ConnectionContext
 */
public final class TRMM_3B42Downloader extends Downloader {
    private final DataDate mDate;
    private final File mOutFile;

    public TRMM_3B42Downloader(DataDate date, File outFile) throws IOException {
        mDate = date;
        mOutFile = outFile;
    }

    /**
     * Builds and returns a list containing all of the available data dates no
     * earlier than the specified start date.
     * 
     * @param startDate
     *            Specifies the inclusive lower bound for the returned data date
     *            list
     * @throws IOException
     */
    public static final List<DataDate> listDates(DataDate startDate)
            throws ConfigReadException, IOException {
        Mode mode = Settings.getMode(DataType.TRMM_3B42);
        if (mode == Mode.FTP) {
            // TODO: Please check the patterns to make sure that they are
            // compatible.
            final Pattern yearDirPattern = Pattern.compile("\\d{4}");
            // final Pattern dayOfYearDirPattern = Pattern.compile("\\d{3}");

            FTPClient ftp = null;
            String rootDir = Config.getInstance().getTRMM_3B42RootDir();

            try {
                ftp =
                        (FTPClient) ConnectionContext.getConnection(mode,
                                DataType.TRMM_3B42);
            } catch (ConnectException e) {
                System.out
                .println("Can't connect to TRMM_3B42 data website, please check your URL.");
                return null;
            }

            try {

                if (!ftp.changeWorkingDirectory(rootDir)) {
                    throw new IOException("Couldn't navigate to directory: "
                            + rootDir);
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
                            String.format("%s/%s", rootDir, yearFile.getName());

                    if (!ftp.changeWorkingDirectory(yearDirectory)) {
                        throw new IOException(
                                "Couldn't navigate to directory: "
                                        + yearDirectory);
                    }

                    // TODO: Create your pattern here
                    Pattern mpattern =
                            Pattern.compile("3B42_daily\\.(\\d{4})\\.(\\d{2})\\.(\\d{2})\\.7\\.bin");

                    for (FTPFile file : ftp.listFiles()) {
                        if (file.isFile()
                                && mpattern.matcher(file.getName()).matches()) {
                            // TODO: Assume following formatis {product
                            // name}.%y4.%m2.%d2.7.bin, please change it as
                            // needed
                            String[] strings = file.getName().split("[.]");
                            final int month = Integer.parseInt(strings[2]);
                            final int day = Integer.parseInt(strings[3]);

                            final DataDate dataDate =
                                    new DataDate(day, month, year);
                            if (dataDate.compareTo(startDate) >= 0) {
                                list.add(dataDate);
                            }
                        }
                    }
                }

                return list;
            } finally {
                FtpClientPool.returnFtpClient(Config.getInstance()
                        .getTRMM_3B42FtpHostName(), ftp);
            }

        } else {
            // create url connection
            URLConnection conn =
                    (URLConnection) ConnectionContext.getConnection(mode,
                            DataType.TRMM_3B42);
            // TODO: Please check the pattern to make sure that it is
            // compatible.
            final Pattern re =
                    Pattern.compile("(\\d{4})\\.(\\d{2})\\.(\\d{2})");
            byte[] downloadPage = null;
            try {
                downloadPage = DownloadUtils.downloadToByteArray(conn);
            } catch (ConnectException e) {
                throw e;
            }

            // Parse it into a DOM tree
            final HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
            Document pagedoc = null;
            try {
                pagedoc = builder.parse(new ByteArrayInputStream(downloadPage));
            } catch (SAXException e) {
                throw new IOException(
                        "Failed to parse the TRMM_3B42 download page", e);
            }

            final NodeList dirlist = pagedoc.getElementsByTagName("a");

            final List<DataDate> list = new ArrayList<DataDate>();

            for (int i = 0; i < dirlist.getLength(); ++i) {
                final String dir =
                        ((Element) dirlist.item(i)).getAttribute("href");

                // Match the filename against the pattern
                final Matcher matcher = re.matcher(dir);
                if (matcher.matches()) {
                    // TODO: Please change this part if you have different
                    // pattern string
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
    public final void download() throws IOException, ConfigReadException,
    DownloadFailedException {
        Mode mode = Settings.getMode(DataType.TRMM_3B42);
        if (mode == Mode.FTP) {
            final FTPClient ftp =
                    (FTPClient) ConnectionContext.getConnection(mode,
                            DataType.TRMM_3B42);
            try {
                // TODO: Change the year directory as needed.
                final String yearDirectory =
                        String.format("%s/%s",
                                Settings.getRootDir(DataType.TRMM_3B42),
                                Integer.toString(mDate.getYear()));
                if (!ftp.changeWorkingDirectory(yearDirectory)) {
                    throw new IOException("Couldn't navigate to directory: "
                            + yearDirectory);
                }
                // TODO: Change the string format as needed.
                String targetFile =
                        String.format("3B42_daily.%04d.%02d.%02d.7.bin",
                                mDate.getYear(), mDate.getMonth(),
                                mDate.getDay());
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
                        .getTRMM_3B42FtpHostName(), ftp);
            }
        } else {
            // mode is http
            // TODO: Create url based on date
            String url_str =
                    Config.getInstance().getTRMM_3B42Url()
                    + String.format("%04d.%02d.%02d", mDate.getYear(),
                            mDate.getMonth(), mDate.getDay());
            // Download the archive
            URL url = new URL(url_str);
            try {
                DownloadUtils.downloadToFile(url, mOutFile);
            } catch (IOException e) { // FIXME: ugly fix so that the system
                // doesn't repeatedly try and fail
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
