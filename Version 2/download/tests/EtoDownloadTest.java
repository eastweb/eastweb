package edu.sdstate.eastweb.prototype.download.tests;

import java.io.IOException;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.download.DownloadFailedException;
import edu.sdstate.eastweb.prototype.download.EtoArchive;
import edu.sdstate.eastweb.prototype.download.EtoDownloader;

public class EtoDownloadTest {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        EtoArchive p=EtoArchive.daily(2013, 12, 1);
        EtoDownloader ed=new EtoDownloader(p);
        try {
            ed.download();
        } catch (ConfigReadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (DownloadFailedException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
