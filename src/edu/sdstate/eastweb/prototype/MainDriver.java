package edu.sdstate.eastweb.prototype;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.download.NLDASDownloader;

public class MainDriver {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

        //String temp = MetaData.GetInstance().IndicesMetaData;

        /*MetaData test=MetaData.GetInstance().get("NLDAS");
        System.out.println( test.Download.mode);*/
        DataDate startDate=new DataDate(10,9,2014);
        List<DataDate> list=NLDASDownloader.listDates(startDate);
        for(DataDate item: list){
            System.out.println(item.toString());
        }
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