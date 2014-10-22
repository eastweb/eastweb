package edu.sdstate.eastweb.prototype.download;

import java.io.IOException;

import edu.sdstate.eastweb.prototype.ConfigReadException;

public class TestDownloadModis {

    public static void main(String[] args) {
        DownloadModuleTests down = new DownloadModuleTests();
        try {
            down.testModisNbar();
        } catch (ConfigReadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
