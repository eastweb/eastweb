package edu.sdstate.eastweb.prototype.download;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.EtoArchive.Type;

public final class EtoDownloader extends Downloader {
    private static final String ETO_YEARLY_ID = "selarchive";
    private static final String ETO_MONTHLY_ID = "selarchive1";
    private static final String ETO_DAILY_ID = "selarchive2";
    private static File tempDictionary;

    private final EtoArchive mArchive;
    // dates will be used by DownloadEtoTask for write download metadata
    private List<DataDate> dates;

    public EtoDownloader(EtoArchive archive) throws IOException {
        mArchive = archive;
        tempDictionary=new File(Config.getInstance().getTempDirectory());
    }

    private static final Document getFewsNetDownloadPage() throws IOException {
        // Download the FEWS NET download page
        URLConnection conn=(URLConnection)ConnectionContext.getConnection(Mode.HTTP, DataType.ETO);
        final byte[] downloadPage = DownloadUtils.downloadToByteArray(conn);

        // Parse it into a DOM tree
        final HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
        try {
            return builder.parse(new ByteArrayInputStream(downloadPage));
        } catch (SAXException e) {
            throw new IOException("Failed to parse the FEWS NET download page", e);
        }
    }

    /**
     * Searches a FEWS NET download page for yearly archives, adding them to the
     * specified list. Returns the most recent DataDate not covered by a yearly archive.
     */
    private static final DataDate listYearlyArchives(Document downloadPage, DataDate startDate,
            List<EtoArchive> list) {
        final Pattern yearlyPattern = Settings.getEtoYearlyPattern();

        DataDate now = DataDate.today().next();

        // Process yearly archives
        final NodeList yearlyOptions = downloadPage
        .getElementById(ETO_YEARLY_ID)
        .getElementsByTagName("option");
        DataDate yearlyArchiveEnd = null;
        for (int i = 0; i < yearlyOptions.getLength(); ++i) {
            final String path = ((Element)yearlyOptions.item(i)).getAttribute("value");

            // Extract the year and month with a regular expression
            final Matcher matcher = yearlyPattern.matcher(path);
            if (matcher.matches()) {
                final int year = Integer.parseInt(matcher.group(1));
                final DataDate lastDate = new DataDate(31, 12, year); // Last day in the year

                // Only add archives that extend past the start date
                if (lastDate.compareTo(startDate) >= 0 && lastDate.compareTo(now) <= 0) {
                    list.add(EtoArchive.yearly(year));

                    // Track the most recent day covered in the yearly archives
                    if (yearlyArchiveEnd == null || lastDate.compareTo(yearlyArchiveEnd) >= 0) {
                        yearlyArchiveEnd = lastDate;
                    }
                }
            }
        }

        if (yearlyArchiveEnd != null) {
            return yearlyArchiveEnd.next();
        } else {
            return startDate;
        }
    }

    /**
     * Searches a FEWS NET download page for monthly archives, adding them to the
     * specified list. Returns the most recent DataDate not covered by a monthly archive.
     */
    private static final DataDate listMonthlyArchives(Document downloadPage, DataDate startDate,
            List<EtoArchive> list) {
        final Pattern monthlyPattern = Settings.getEtoMonthlyPattern();

        DataDate now = DataDate.today().next();

        // Process monthly archives
        final NodeList monthlyOptions = downloadPage
        .getElementById(ETO_MONTHLY_ID)
        .getElementsByTagName("option");
        DataDate monthlyArchiveEnd = null;
        for (int i = 0; i < monthlyOptions.getLength(); ++i) {
            final String path = ((Element)monthlyOptions.item(i)).getAttribute("value");

            // Extract the year and month with a regular expression
            final Matcher matcher = monthlyPattern.matcher(path);
            if (matcher.matches()) {
                final int year = Integer.parseInt(matcher.group(1));
                final int month = Integer.parseInt(matcher.group(2));
                final DataDate lastDate = new DataDate(1, month, year).lastDayOfMonth();

                // Only add archives that extend past the start date
                if (lastDate.compareTo(startDate) >= 0 && lastDate.compareTo(now) <= 0) {
                    list.add(EtoArchive.monthly(year, month));

                    // Track the most recent day covered in the monthly archives
                    if (monthlyArchiveEnd == null || lastDate.compareTo(monthlyArchiveEnd) >= 0) {
                        monthlyArchiveEnd = lastDate;
                    }
                }
            }
        }

        if (monthlyArchiveEnd != null) {
            return monthlyArchiveEnd.next();
        } else {
            return startDate;
        }
    }

    /**
     * Searches a FEWS NET download page for daily archives, adding them to the
     * specified list.
     */
    private static final void listDailyArchives(Document downloadPage, DataDate startDate,
            List<EtoArchive> list) {
        final Pattern dailyPattern = Settings.getEtoDailyPattern();

        DataDate now = DataDate.today().next();

        final NodeList dailyOptions = downloadPage
        .getElementById(ETO_DAILY_ID)
        .getElementsByTagName("option");
        for (int i = 0; i < dailyOptions.getLength(); ++i) {
            final String path = ((Element)dailyOptions.item(i)).getAttribute("value");

            // Extract the year and month with a regular expression
            final Matcher matcher = dailyPattern.matcher(path);
            if (matcher.matches()) {
                final int year = Integer.parseInt(matcher.group(1)) + 2000; // Group only contains two digits
                final int month = Integer.parseInt(matcher.group(2));
                final int day = Integer.parseInt(matcher.group(3));

                try {
                    final DataDate lastDate = new DataDate(day, month, year);

                    // Only add newer archives that extend past the start date
                    if (lastDate.compareTo(startDate) >= 0 && lastDate.compareTo(now) <= 0) {
                        list.add(EtoArchive.daily(year, month, day));
                    }
                } catch (IllegalArgumentException e) { // The ETo portal occasionally provides non-existent dates.
                    System.out.println(String.format("ETo portal provided non-existent date: %d-%d-%d", month, day, year));
                }
            }
        }
    }

    public static final List<EtoArchive> listArchives(DataDate startDate) throws IOException {
        final Document downloadPage = getFewsNetDownloadPage();
        final List<EtoArchive> list = new ArrayList<EtoArchive>();

        // List yearly archives and shift the start date forward
        startDate = DataDate.max(startDate, listYearlyArchives(
                downloadPage, startDate, list));

        // List monthly archives and shift the start date forward
        startDate = DataDate.max(startDate, listMonthlyArchives(
                downloadPage, startDate, list));

        // List daily archives
        listDailyArchives(downloadPage, startDate, list);

        // Sort the list and we're done
        Collections.sort(list);
        return list;
    }

    @Override
    public final void download() throws IOException, ConfigReadException, DownloadFailedException {
        final String requestFormat =Settings.getEtoRequestFormat();

        String request;
        switch (mArchive.getType()) {
        case Yearly:
            request = String.format(
                    requestFormat,
                    String.format("global/pet/years/pet_%04d.tar.gz", mArchive.getYear()),
                    "",
                    ""
            );
            break;

        case Monthly:
            request = String.format(
                    requestFormat,
                    "",
                    String.format("global/pet/months/pet_%04d%02d.tar.gz",
                            mArchive.getYear(), mArchive.getMonth()),
                            ""
            );

            break;

        case Daily:
            request = String.format(
                    requestFormat,
                    "",
                    "",
                    String.format("global/pet/days/et%02d%02d%02d.tar.gz",
                            mArchive.getYear() % 100, mArchive.getMonth(), mArchive.getDay())
            );

            break;

        default:
            throw new IOException("Invalid archive type");
        }

        final URL url = new URL(Config.getInstance().getEtoHttpUrl());

        // Download the archive to a temporary file
        final File archiveFile = File.createTempFile("EASTWeb", ".tar.gz",tempDictionary);
        OutputStreamWriter writer = null;
        try {
            // Write POST request
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(request);
            writer.flush();

            // Download the archive
            DownloadUtils.downloadToFile(conn, archiveFile);
            //check the size of monthly zip file. If it's small than 500k, throw down load failed exception
            //fix the incorrect error message "format is not gzip". by Jiameng Hu 2013/11/28
            if(mArchive.getType()==EtoArchive.Type.Monthly){
                long Bytes=archiveFile.length();
                if(Bytes<500000){
                    throw new DownloadFailedException("Fail to download Eto monthly file");
                }
            }

            System.out.println(archiveFile.canRead());
            if(!archiveFile.canRead()){
                throw new DownloadFailedException("download failed: "+mArchive.getYear()+" "+mArchive.getMonth()+" "+mArchive.getDay());
            }

            // Unzip the archive
            if (mArchive.getType() == Type.Daily) {
                // Extract the daily archive to the target directory
                final File destDir = DirectoryLayout.getEtoDownloadDir(mArchive.toDataDate());
                extractTgzToDirectory(archiveFile, destDir);
                FileUtils.forceDelete(archiveFile);

                final List<DataDate> dates = new ArrayList<DataDate>();
                dates.add(mArchive.toDataDate());
                this.dates= dates;
            } else {
                //return extractEtoNestedArchive(archiveFile);

                dates= extractEtoNestedArchive(archiveFile);

            }
        } finally {
            FileUtils.deleteQuietly(archiveFile);
            if (writer != null) {
                writer.close();
            }
        }
    }

    public List<DataDate> getDates(){
        return dates;

    }

    private static final List<DataDate> extractEtoNestedArchive(File archiveFile)
    throws IOException, ConfigReadException
    {
        final Pattern pattern = Pattern.compile("et(\\d{2})(\\d{2})(\\d{2}).tar.gz");

        // Create a temporary directory
        final File tempDir = File.createTempFile("ETo", null,tempDictionary);
        FileUtils.forceDelete(tempDir);
        FileUtils.forceMkdir(tempDir);
        try {
            // Extract the archive
            final List<File> files = extractTgzToDirectory(archiveFile, tempDir);
            FileUtils.forceDelete(archiveFile);

            // Identify and extract each daily archive
            final List<DataDate> dates = new ArrayList<DataDate>();
            try{
                for (File dailyArchive : files) {
                    final Matcher matcher = pattern.matcher(dailyArchive.getName());
                    if (!matcher.matches()) {
                        throw new IOException("Unexpected filename in ETo archive: " +
                                dailyArchive.getName());
                    }

                    // Parse the date out of the daily archive's filename
                    final int year = Integer.parseInt(matcher.group(1)) + 2000; // Group only contains two digits
                    final int month = Integer.parseInt(matcher.group(2));
                    final int day = Integer.parseInt(matcher.group(3));
                    final DataDate date = new DataDate(day, month, year);
                    dates.add(date);

                    // Extract the daily archive
                    final File destDir = DirectoryLayout.getEtoDownloadDir(date);
                    extractTgzToDirectory(dailyArchive, destDir);
                }

            }catch (NullPointerException e){
                throw e;
            }
            return dates;
        } finally {
            FileUtils.deleteQuietly(tempDir);
        }
    }


    /**
     * Extracts a .tar.gz compressed archive to the specified directory, returning a list of the
     * extracted files. Directory structure is not preserved.
     * @throws DownloadFailedException
     */
    private static final List<File> extractTgzToDirectory(File archiveFile, File destDir)
    throws IOException
    {
        FileUtils.forceMkdir(destDir);

        final FileInputStream fis = new FileInputStream(archiveFile);
        Closeable closeable = fis;
        final List<File> files = new ArrayList<File>();
        try {
            final GZIPInputStream gis = new GZIPInputStream(fis);
            closeable = gis;

            final TarArchiveInputStream tais = new TarArchiveInputStream(gis);
            closeable = tais;

            TarArchiveEntry tarEntry;
            while ((tarEntry = tais.getNextTarEntry()) != null) {
                if (tarEntry.isDirectory()) {
                    continue;
                }

                final File destFile = new File(destDir, new File(tarEntry.getName()).getName());
                files.add(destFile);

                final FileOutputStream outStream = new FileOutputStream(destFile);
                try {
                    // Just being pedantic
                    if (tarEntry.getSize() > Integer.MAX_VALUE) {
                        throw new IOException("Archive entry is too large");
                    }
                    int numBytesRemaining = (int)tarEntry.getSize();

                    final byte[] buffer = new byte[4096];
                    int numBytesRead;
                    while ((numBytesRead = tais.read(buffer, 0, Math.min(buffer.length, numBytesRemaining))) > 0) {
                        outStream.write(buffer, 0, numBytesRead);
                        numBytesRemaining -= numBytesRead;
                    }
                } finally {
                    outStream.close();
                }
            }


        }
        //        catch(IOException e){
        //            throw new DownloadFailedException("download failed: ");
        //        }

        finally {
            closeable.close();
        }
        return files;
    }
}