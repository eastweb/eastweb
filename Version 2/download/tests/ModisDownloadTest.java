package edu.sdstate.eastweb.prototype.download.tests;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.ModisTile;
import edu.sdstate.eastweb.prototype.database.Database;
import edu.sdstate.eastweb.prototype.download.DownloadUtils;
import edu.sdstate.eastweb.prototype.download.ModisDownloader;
import edu.sdstate.eastweb.prototype.download.ModisLstDownloader;
import edu.sdstate.eastweb.prototype.download.Settings;

public class ModisDownloadTest {
    public static void main(String[] args) throws Exception {

        ModisTile mt=new ModisTile(10,5);
        DataDate md=new DataDate(05,03,2000);
        DataDate mp=new DataDate(20,8,2007);
        //File file=new File("/Users/jiameng/Desktop/modis.tiff");
        ModisLstDownloader mdd=new ModisLstDownloader(md,mt,mp);
        mdd.download();
        /*final Pattern re = Pattern.compile(String.format(
                "%s\\.A%04d%03d\\.h%02dv%02d\\.005\\.\\d{13}\\.hdf",
                "MOD11A2",
                2000,
                65,
                10,
                5
                // mProcessed.getYear(),
                // mProcessed.getDayOfYear()
                ));
        String dir="MOD11A2.A2000065.h10v05.005.2007176174107.hdf";
        System.out.println(re.matcher(dir).matches());*/

    }

}
