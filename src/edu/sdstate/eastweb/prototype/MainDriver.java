package edu.sdstate.eastweb.prototype;

import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.download.NldasDownloadTask;
import edu.sdstate.eastweb.prototype.download.NldasDownloader;

public class MainDriver {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        DataDate start= new DataDate(12,10,2014);
        NldasDownloadTask nt=new NldasDownloadTask(start);
        nt.run();

        //String temp = MetaData.GetInstance().IndicesMetaData;

        /*MetaData test=MetaData.GetInstance().get("NLDAS");
        System.out.println( test.Download.mode);*/
        /*    DataDate startDate=new DataDate(10,9,2014);
        List<DataDate> list=NldasDownloader.listDates(startDate);
        for(DataDate item: list){
            System.out.println(item.toString());
        }*/


        /*
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // run stuff on back ground
            }
        };*/

        //   new Thread(r).start();

    }
}

