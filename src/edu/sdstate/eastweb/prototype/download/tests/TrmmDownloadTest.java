package edu.sdstate.eastweb.prototype.download.tests;

import java.io.File;
import java.io.IOException;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.DownloadFailedException;
import edu.sdstate.eastweb.prototype.download.EtoArchive;
import edu.sdstate.eastweb.prototype.download.EtoDownloader;
import edu.sdstate.eastweb.prototype.download.NLDASDownloader;
import edu.sdstate.eastweb.prototype.download.TRMM_3B42Downloader;
import edu.sdstate.eastweb.prototype.download.TrmmDownloader;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;

public class TrmmDownloadTest {
    public static void main(String[] args) throws IOException {
        DataDate dd=new DataDate(1,25,10,2013);
        //TrmmDownloader td=new TrmmDownloader(TrmmProduct.TRMM_3B42,dd,new File("c:\\trmm.bin"));
        NLDASDownloader td=new NLDASDownloader(dd,new File("c:\\nlsasd.grb"));
        try {
            td.download();
        } catch (DownloadFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //        String[] list=new String[5];
        //        list[0]="hello";
        //        list[1]="world";
        //        System.out.println(list.length);
        //        for(String item : list){
        //            System.out.println(item);
        //        }
    }

}
