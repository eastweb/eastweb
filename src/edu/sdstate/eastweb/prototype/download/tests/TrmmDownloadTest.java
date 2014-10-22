package edu.sdstate.eastweb.prototype.download.tests;

import java.io.File;
import java.io.IOException;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.DownloadFailedException;
import edu.sdstate.eastweb.prototype.download.NldasDownloader;

public class TrmmDownloadTest {
    public static void main(String[] args) throws IOException {
        DataDate dd=new DataDate(1,25,10,2013);
        //TRMM_3B42Downloader td=new TRMM_3B42Downloader(dd,new File("/Users/jiameng/Desktop/trmm.bin"));
        NldasDownloader td=new NldasDownloader(dd,new File("c:\\nlsasd.grb"));
        try {
            td.download();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
