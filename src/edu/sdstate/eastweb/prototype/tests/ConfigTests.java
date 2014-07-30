package edu.sdstate.eastweb.prototype.tests;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;

public class ConfigTests {

    /**
     * @param args
     * @throws ConfigReadException
     */
    public static void main(String[] args) throws ConfigReadException {
        // TODO Auto-generated method stub
        System.out.println(Config.getInstance().getRootDirectory());
        System.out.println(Config.getInstance().getTempDirectory());

        System.out.println(Config.getInstance().getModisFtpHostName());
        System.out.println(Config.getInstance().getModisDownMode());
        System.out.println(Config.getInstance().getModisLstRootDir());
        System.out.println(Config.getInstance().getModisNbarRootDir());
        //
        System.out.println(Config.getInstance().getEtoDownMode());
        System.out.println(Config.getInstance().getEtoHttpUrl());

        System.out.println(Config.getInstance().getTrmmDownMode());
        System.out.println(Config.getInstance().getTrmmFtpHostName());
        // System.out.println(Config.getInstance().getTrmmRootDir());

        System.out.println(Config.getInstance().getDatabaseHost());
        System.out.println(Config.getInstance().getDatabaseUsername());
        System.out.println(Config.getInstance().getDatabasePassword());

        System.out.println(Config.getInstance().getDownloadRefreshDays());


    }

}
