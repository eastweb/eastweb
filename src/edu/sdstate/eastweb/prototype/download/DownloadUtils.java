package edu.sdstate.eastweb.prototype.download;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

/**
 * Provides services to download files over FTP and HTTP connections to files, {@link OutputStream}s, and to
 * byte arrays.
 * 
 * @author Michael VanBemmel
 */
public final class DownloadUtils {
    private DownloadUtils() {
    }

    /**
     * Downloads a remote file to a local file using an FTP client.
     * The local file is deleted if the download fails.
     * @param ftp FTP client to use
     * @param remoteFilename Remote filename
     * @param localFile Local filename
     * @throws IOException
     */
    public static final void download(FTPClient ftp, String remoteFilename, File localFile) throws IOException
    {
        System.out.println(remoteFilename);
        final OutputStream outStream = new FileOutputStream(localFile);
        try {
            if (!ftp.retrieveFile(remoteFilename, outStream)) {
                throw new IOException("Download failed");
            }
            outStream.close();
        } catch (IOException e) {
            IOUtils.closeQuietly(outStream);
            FileUtils.deleteQuietly(localFile);
            throw e;
        }
    }

    public static final void downloadToStream(URL url, OutputStream outStream) throws IOException {
        URLConnection conn = url.openConnection();
        downloadToStream(conn, outStream);
    }

    public static final void downloadToStream(URLConnection conn, OutputStream outStream) throws IOException {
        if (conn instanceof HttpURLConnection) {
            final int code = ((HttpURLConnection)conn).getResponseCode();
            if (code != 200) {
                throw new IOException("HTTP request returned code " + code);
            }
        }

        final BufferedInputStream inStream = new BufferedInputStream(conn.getInputStream());
        try {
            final byte[] buffer = new byte[4096];
            int numBytesRead;
            while ((numBytesRead = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, numBytesRead);
            }
        } finally {
            inStream.close();
        }
    }

    /**
     * Downloads a URL to a local file.
     * The local file is deleted if the download fails.
     * @param connection connection to download from
     * @param localFilename Local filename
     * @throws IOException
     */
    public static final void downloadToFile(URLConnection connection, File localFile) throws IOException {
        final OutputStream outStream = new FileOutputStream(localFile);
        try {
            downloadToStream(connection, outStream);
            outStream.close();
        } catch (IOException e) {
            IOUtils.closeQuietly(outStream);
            FileUtils.deleteQuietly(localFile);
            throw e;
        }
    }

    /**
     * Downloads a URL to a local file.
     * The local file is deleted if the download fails.
     * @param url URL to download
     * @param localFilename Local filename
     * @throws IOException
     */
    public static final void downloadToFile(URL url, File localFile) throws IOException {
        final BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(localFile));
        try {
            downloadToStream(url, outStream);
            outStream.close();
        } catch (IOException e) {
            IOUtils.closeQuietly(outStream);
            FileUtils.deleteQuietly(localFile);
            throw e;
        }
    }

    public static final byte[] downloadToByteArray(URL url) throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        downloadToStream(url, outStream);
        return outStream.toByteArray();
    }

    public static final byte[] downloadToByteArray(URLConnection conn) throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        downloadToStream(conn, outStream);
        return outStream.toByteArray();
    }
}
