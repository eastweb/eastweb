package edu.sdstate.eastweb.prototype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class MainDriver {

    public static void main(final String[] args) throws Exception {

        String temp = MetaData.GetInstance().IndicesMetaData;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {

                    Server(args);

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
        Client(args);
    }


}
}

