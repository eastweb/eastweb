package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

/*
 * ModisTile class stores bands and metadata information for given modis tile
 * file. Programmer: Jiameng Hu Date: 17/03/2013
 */
public class ModisTileData {
    int horizon;
    int vertical;
    int bandNumber;
    int xSize;
    int ySize;
    String[] sdsName;
    ArrayList<ImageArray> imageSet;

    ModisTileData(int bandNumber, int x, int y) {
        this.bandNumber = bandNumber;
        xSize = x;
        ySize = y;

    }

    ModisTileData(File file) throws InterruptedException {
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {
            imageSet=new  ArrayList<ImageArray>();
            Dataset hdf = null;
            hdf = gdal.Open(file.toString());
            Hashtable<String, String> sdsdict=null;
            //check if hdf is null or not  for testing
            if(hdf!=null){
                try{sdsdict = hdf.GetMetadata_Dict("SUBDATASETS");

                }
                catch (NullPointerException e){
                    System.out.println("hdf open error, null pointer" +file.toString());
                }
            }else{
                System.out.println("hdf is null:"+ file.toString());
            }
            bandNumber=sdsdict.size()/2;
            horizon =
                Integer.parseInt(hdf
                        .GetMetadataItem("HORIZONTALTILENUMBER"));
            vertical =
                Integer.parseInt(hdf.GetMetadataItem("VERTICALTILENUMBER"));
            sdsName = new String[bandNumber];

            // save all the band names into sdsName[] array in the order of band number
            Enumeration<String> keys = sdsdict.keys();
            while (keys.hasMoreElements()) {
                Object aKey = keys.nextElement();
                Object aValue = sdsdict.get(aKey);
                // System.out.println("Key: \""+aKey.toString()+"\" has value of: \""
                //         +aValue.toString()+"\"");
                if (aKey.toString().contains("NAME")) {
                    String bandName[] = aKey.toString().split("_");
                    //System.out.println(bandName.length);
                    int i = Integer.parseInt(bandName[1]);
                    //System.out.println(i);
                    sdsName[i - 1] = aValue.toString();
                    // System.out.println(i+"  "+sdsName[i-1]);

                }
                hdf.delete();
            }
            Dataset temp=gdal.Open(sdsName[0]);
            xSize = temp.getRasterXSize();
            ySize = temp.getRasterYSize();
            temp.delete();
        }
    }

}
