package edu.sdstate.eastweb.prototype.download;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.*;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import org.apache.commons.net.ftp.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;

/**
 * Implements the MODIS NBAR and LST components of the download module. The concrete classes
 * {@link ModisNbarDownloader} and {@link ModisLstDownloader} implement the static methods
 * {@code getRootDir()} and {@code getProduct()}, completing the implementation.
 * 
 * @author Dan Woodward
 * @author Michael VanBemmel
 * @author Yi Liu.  Modified the FTP to HTTP on 6/7/13
 * @author jiameng Hu. Catch and throw connectException on 10/19/13
 */
public abstract class ModisDownloader {

    private static final String getModisDateDir(String rootDir, DataDate date) {
        return String.format("%s/%04d.%02d.%02d", rootDir, date.getYear(), date.getMonth(), date.getDay());
    }

    private final DataDate mDate;
    private final ModisTile mTile;
    private final DataDate mProcessed;
    private final File mOutFile;


    public ModisDownloader(DataDate date, ModisTile tile, DataDate processed, File outFile) throws ConfigReadException {
        mDate = date;
        mTile = tile;
        mProcessed = processed;
        mOutFile = outFile;

    }

    /**
     * Builds and returns a list containing all of the available data dates no earlier than the
     * specified start date.
     * @param startDate If non-null, specifies the inclusive lower bound for the returned data date
     * list; if null, data dates are not filtered
     * @throws IOException
     */
    protected static final List<DataDate> listDates(DataDate startDate, Object conn)
    throws IOException {
        Mode mode=Settings.getMode(DataType.MODIS);
        if(mode==Mode.HTTP){
            //final Pattern re = Pattern.compile("(\\d{4})\\.(\\d{2})\\.(\\d{2})");

            // pattern is changed to be d{4}.d{2}.d{2}/ with http href
            // somthing likes like "2000.02.18/"
            final Pattern re = Pattern.compile(Settings.getModisDateStr());


            //final URL url = new URL(ModisUrl);

            // list the files
            // catch and throw connectionException. by Jiameng Hu 10/19/13
            byte[] downloadPage=null;
            try{
                downloadPage = DownloadUtils.downloadToByteArray((URLConnection)conn);
            }catch(ConnectException e){
                throw e;
            }


            // Parse it into a DOM tree
            final HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
            Document pagedoc = null;
            try {
                pagedoc = builder.parse(new ByteArrayInputStream(downloadPage));
            } catch (SAXException e) {
                throw new IOException("Failed to parse the Modis download page", e);
            }

            final NodeList dirlist = pagedoc.getElementsByTagName("a");

            final List<DataDate> list = new ArrayList<DataDate>();

            for (int i = 0; i < dirlist.getLength(); ++i) {
                final String dir = ((Element)dirlist.item(i)).getAttribute("href");

                // Match the filename against the known pattern of a MODIS NBAR date directory
                final Matcher matcher = re.matcher(dir);
                if (matcher.matches()) {
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

        }else{
            //TODO:FTP
            return null;
        }

    }

    /**
     * Builds and returns a map from each of the available MODIS tiles on the specified data date to
     * its processed date.
     * @param date Specifies the data date
     * @throws IOException
     */
    protected static final Map<ModisTile, DataDate> listTiles(DataDate date, String rootDir,
            String product) throws IOException {
        Mode mode=Settings.getMode(DataType.MODIS);
        if(mode==Mode.HTTP){
            final Pattern re = Pattern.compile(String.format(
                    Settings.getModisListTilesStr(),
                    product, date.getYear(), date.getDayOfYear()));

            // changed to http protocol.  6/7/13 by Y.L.
            //Remove http:// and modishostname. add it into getModisDateDir method call  by Jiameng Hu
            String url_str = getModisDateDir(rootDir, date);
            final URL url = new URL(url_str);

            // list the files
            // catch and throw connectionException. by Jiameng Hu 10/19/13
            byte[] downloadPage=null;
            try{
                downloadPage = DownloadUtils.downloadToByteArray(url);
            }catch(ConnectException e){
                throw e;
            }

            // Parse it into a DOM tree
            final HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
            Document pagedoc = null;
            try {
                pagedoc = builder.parse(new ByteArrayInputStream(downloadPage));
            } catch (SAXException e) {
                throw new IOException("Failed to parse the Modis download page", e);
            }

            final NodeList dirlist = pagedoc.getElementsByTagName("a");

            final Map<ModisTile, DataDate> map = new HashMap<ModisTile, DataDate>();

            for (int i = 0; i < dirlist.getLength(); ++i) {
                final String dir = ((Element)dirlist.item(i)).getAttribute("href");

                // Match the filename against the known pattern of a MODIS tile directory
                final Matcher matcher = re.matcher(dir);
                if (matcher.matches()) {
                    final int hTile = Integer.parseInt(matcher.group(1));
                    final int vTile = Integer.parseInt(matcher.group(2));
                    final int year = Integer.parseInt(matcher.group(3));
                    final int day = Integer.parseInt(matcher.group(4));
                    final DataDate processed = new DataDate(day, year);
                    map.put(new ModisTile(hTile, vTile), processed);
                }
            }
            return map;
        }else{
            //TODO:FTP
            return null;
        }

    }


    /**
     * Downloads a tile to the specified file.
     * @param date Tile data date
     * @param tile Tile location
     * @param outFile Destination file
     * @throws IOException
     */
    public final void download() throws IOException {
        Mode mode=Settings.getMode(DataType.MODIS);
        if(mode==Mode.HTTP){
            // Build a regular expression that will match any HDF files with the specified date and tile
            // (and any processing date)

            final Pattern re = Pattern.compile(String.format(
                    Settings.getModisDownloadStr(),
                    getProduct(),
                    mDate.getYear(),
                    mDate.getDayOfYear(),
                    mTile.getHTile(),
                    mTile.getVTile(),
                    mProcessed.getYear(),
                    mProcessed.getDayOfYear()
            ));

            // changed to use http protocal on 6/7/13 --- Y.L.
            String url_str = getModisDateDir(getRootDir(), mDate);
            URL url = new URL(url_str);

            // list the files
            // catch and throw connectionException. by Jiameng Hu 10/19/13
            byte[] downloadPage=null;
            try{
                downloadPage = DownloadUtils.downloadToByteArray(url);
            }catch(ConnectException e){
                throw e;
            }

            // Parse it into a DOM tree
            final HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
            Document pagedoc = null;
            try {
                pagedoc = builder.parse(new ByteArrayInputStream(downloadPage));
            } catch (SAXException e) {
                throw new IOException("Failed to parse the Modis download page", e);
            }

            final NodeList dirlist = pagedoc.getElementsByTagName("a");

            try {
                // List files and select the best match
                String bestMatch = null;
                for (int i = 0; i < dirlist.getLength(); ++i) {
                    final String dir = ((Element)dirlist.item(i)).getAttribute("href");

                    if (re.matcher(dir).matches() &&
                            (bestMatch == null || dir.compareTo(bestMatch) > 0)) {
                        // This file is either the first match or has a more
                        // recent processing date than the current best match
                        bestMatch = dir;
                    }
                }

                if (bestMatch == null) {
                    throw new FileNotFoundException();
                }

                url_str += "/" + bestMatch;
                // Download the archive
                url = new URL(url_str);

                DownloadUtils.downloadToFile(url, mOutFile);

            } catch (IOException e) { // FIXME: ugly fix so that the system doesn't repeatedly try and fail
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                throw e;
            }

        }else{
            //TODO:FTP
        }


    }

    /**
     * Implementers must override this method to return the root directory of the
     * data archive on the FTP server.
     */
    protected abstract String getRootDir();

    /**
     * Implementers must override this method to return the name of the MODIS product
     * to be downloaded.
     */
    protected abstract String getProduct();

    /**
     * Builds and returns a list containing all of the available data dates no earlier than the
     * specified start date.
     * @param product Specifies the MODIS product
     * @param startDate If non-null, specifies the inclusive lower bound for the returned data date
     * list; if null, data dates are not filtered
     * @throws IOException
     */
    public static List<DataDate> listDates(ModisProduct product, DataDate startDate)
    throws IOException
    {
        switch (product) {
        case NBAR:
            return ModisNbarDownloader.listDates(startDate);

        case LST:
            return ModisLstDownloader.listDates(startDate);

        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * Builds and returns a map from each of the available MODIS tiles on the specified data date to
     * its processed date.
     * @param product Specifies the MODIS product
     * @param date Specifies the data date
     * @throws IOException
     */
    public static Map<ModisTile, DataDate> listTiles(ModisProduct product, DataDate date)
    throws IOException
    {
        switch (product) {
        case NBAR:
            return ModisNbarDownloader.listTiles(date);

        case LST:
            return ModisLstDownloader.listTiles(date);

        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns a new instance of either ModisNbarDownloader or ModisLstDownloader, depending on
     * the specified product.
     */
    public static ModisDownloader newWithProduct(ModisId modisId) throws ConfigReadException {
        switch (modisId.getProduct()) {
        case NBAR:
            return new ModisNbarDownloader(modisId.getDate(), modisId.getTile(),
                    modisId.getProcessed());

        case LST:
            return new ModisLstDownloader(modisId.getDate(), modisId.getTile(),
                    modisId.getProcessed());

        default:
            throw new IllegalArgumentException();
        }
    }
}