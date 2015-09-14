package edu.sdstate.eastweb.prototype.download;

import java.util.regex.Pattern;
import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.Downloader.DataType;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;


public class Settings {

    static Mode getMode(DataType d) throws ConfigReadException{
        switch(d){
        case TRMM:
            return Config.getInstance().getTrmmDownMode();
        case ETO:
            return Config.getInstance().getEtoDownMode();
        case MODIS:
            return Config.getInstance().getModisDownMode();
        case TRMM_3B42:
            return Config.getInstance().getTRMM_3B42DownMode();
        case NLDAS:
            return Config.getInstance().getNLDASDownMode();
        default:
            throw new IllegalArgumentException("Data type " + d + " is not supported to get download protocol type.");
        }

    }

    static String getHostName(DataType d) throws ConfigReadException{
        switch(d){
        case TRMM:
            return Config.getInstance().getTrmmFtpHostName();
        case ETO:
            return Config.getInstance().getEtoFtpHostName();
        case MODIS:
            return Config.getInstance().getModisFtpHostName();
        case TRMM_3B42:
            return Config.getInstance().getTRMM_3B42FtpHostName();
        case NLDAS:
            return Config.getInstance().getNLDASFtpHostName();
        default:
            throw new IllegalArgumentException("Data type " + d + " is not supported to get hostname.");
        }

    }

    static String getUserName(DataType d) throws ConfigReadException{
        switch(d){
        case TRMM:
            return Config.getInstance().getTrmmUserName();
        case ETO:
            return Config.getInstance().getEtoUserName();
        case MODIS:
            return Config.getInstance().getModisUserName();
        case TRMM_3B42:
            return Config.getInstance().getTRMM_3B42UserName();
        case NLDAS:
            return Config.getInstance().getNLDASUserName();
        default:
            throw new IllegalArgumentException("Data type " + d + " is not supported to get username.");
        }

    }

    static String getPassword(DataType d) throws ConfigReadException{
        switch(d){
        case TRMM:
            return Config.getInstance().getTrmmPassWord();
        case ETO:
            return Config.getInstance().getEtoPassWord();
        case MODIS:
            return Config.getInstance().getModisPassWord();
        case TRMM_3B42:
            return Config.getInstance().getTRMM_3B42PassWord();
        case NLDAS:
            return Config.getInstance().getNLDASPassWord();
        default:
            throw new IllegalArgumentException("Data type " + d + " is not supported to get password.");
        }

    }

    static String getUrl(DataType d) throws ConfigReadException{
        switch(d){
        case TRMM:
            return Config.getInstance().getTrmmHttpUrl();
        case ETO:
            return Config.getInstance().getEtoHttpUrl();
        case NLDAS:
            return Config.getInstance().getNLDASUrl();
        default:
            throw new IllegalArgumentException("Can't get url for Data type " + d );
        }

    }

    static String getRootDir(DataType d) throws ConfigReadException{
        switch (d) {
        case TRMM_3B42:
            return Config.getInstance().getTRMM_3B42RootDir();
        case NLDAS:
            return Config.getInstance().getNLDASRootDir();
        default:
            throw new IllegalArgumentException("Can't get rootdir for Data type " + d);
        }
    }


    //return the root dictionary of Trmm product
    static String getRootDir(TrmmProduct product) throws ConfigReadException{
        switch (product) {
        case TRMM_3B42:
            return Config.getInstance().getTrmm3b42RootDir();
        case TRMM_3B42RT:
            return Config.getInstance().getTrmm3b42rtRootDir();
        default:
            throw new IllegalArgumentException("Product " + product + " not supported.");
        }
    }



    //return the pattern of Trmm product
    static Pattern getTrmmPattern(TrmmProduct product){
        switch (product) {
        case TRMM_3B42:
            return Pattern.compile("3B42_daily\\.(\\d{4})\\.(\\d{2})\\.(\\d{2})\\.7\\.bin");
        case TRMM_3B42RT:
            return Pattern.compile("3B42RT_daily\\.(\\d{4})\\.(\\d{2})\\.(\\d{2})\\.bin");
        default:
            throw new IllegalArgumentException("Product " + product + " not supported.");
        }
    }


    //return the download file format string of Modis product
    static String getModisDownloadStr(){

        return "%s\\.A%04d%03d\\.h%02dv%02d\\.005\\.%04d%03d\\d{6}\\.hdf";

    }
    //return the list tiles format string of Modis product
    static String getModisListTilesStr(){
        return  "%s\\.A%04d%03d\\.h(\\d{2})v(\\d{2})\\.005\\.(\\d{4})(\\d{3})\\d{6}\\.hdf";
    }

    //return the date format string of Modis product
    static String getModisDateStr(){
        return "(\\d{4})\\.(\\d{2})\\.(\\d{2})(/)";
    }


    //return the download file name of Trmm product
    static String getFilename(TrmmProduct product, DataDate date) {
        switch (product) {
        case TRMM_3B42:
            return String.format(
                    "3B42_daily.%04d.%02d.%02d.7.bin",
                    date.getYear(),
                    date.getMonth(),
                    date.getDay()
            );
        case TRMM_3B42RT:
            return String.format(
                    "3B42RT_daily.%04d.%02d.%02d.bin",
                    date.getYear(),
                    date.getMonth(),
                    date.getDay()
            );
        default:
            throw new IllegalArgumentException("Product " + product + " not supported.");
        }
    }

    //return Eto yearlyPattern
    public static Pattern getEtoYearlyPattern() {
        return Pattern.compile("global/pet/years/pet_(\\d{4})\\.tar\\.gz");
    }

    public static Pattern getEtoMonthlyPattern() {

        return Pattern.compile("global/pet/months/pet_(\\d{4})(\\d{2})\\.tar\\.gz");
    }

    public static Pattern getEtoDailyPattern() {

        return Pattern.compile("global/pet/days/et(\\d{2})(\\d{2})(\\d{2})\\.tar\\.gz");
    }

    public static String getEtoRequestFormat() {
        return "selarchive=%s&" +
        "selarchive1=%s&" +
        "selarchive2=%s&" +
        "image=nd&" +
        "extent=af&" +
        "z=&" +
        "server_store=http://earlywarning.usgs.gov/";
    }




}
