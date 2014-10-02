package edu.sdstate.eastweb.prototype;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class MainDriver {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

        String temp = MetaData.GetInstance().IndicesMetaData;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // run stuff on back ground
            }
        };

        new Thread(r).start();

    }
}

