package edu.sdstate.eastweb.prototype;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import edu.sdstate.eastweb.prototype.download.NLDASDownloader;

public class MainDriver {

    public static void main(final String[] args) throws Exception {

        //String temp = MetaData.GetInstance().IndicesMetaData;

        /*MetaData test=MetaData.GetInstance().get("NLDAS");
        System.out.println( test.Download.mode);*/
        DataDate startDate=new DataDate(10,9,2014);
        List<DataDate> list=NLDASDownloader.listDates(startDate);
        for(DataDate item: list){
            System.out.println(item.toString());
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {

                    UdpProtocol.Instance().Server("main driver", "message");

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("RECEIVED: ");
            }
        };

        new Thread(r).start();

        Thread.sleep(5000);
        UdpProtocol.Instance().Client("main driver", "message");
    }
}

