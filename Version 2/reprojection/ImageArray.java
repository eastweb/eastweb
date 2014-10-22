package edu.sdstate.eastweb.prototype.reprojection;

/* ImageArray class stores the array produced by Gdal for given band.
 * It also provides the getRow(), and setRow() method for image operation.
 * Programmer: Jiameng Hu
 * Date: 17/03/2013
 */

import org.gdal.gdal.Band;

import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class ImageArray {
    private double [] array;
    private int xSize;
    private int ySize;

    ImageArray(Band band){
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {
            xSize=band.getXSize();
            ySize=band.getYSize();
            array=new double[xSize*ySize];
            //System.out.println("before read array, xSize= "+xSize+"ySize= "+ySize);
            band.ReadRaster(0, 0, xSize, ySize, array);
            //System.out.println("after read array");
        }
    }

    ImageArray(int x, int y){
        xSize=x;
        ySize=y;
        array=new double[x*y];
    }

    public double[] getArray(){
        return array;
    }

    public double[] getRow(int rowNumber){
        if(rowNumber<0 || rowNumber>ySize){
            System.out.println("row number out of range: "+ rowNumber);
            return null;
        }else{
            double[] row=new double[xSize];
            for(int i=rowNumber*xSize, j=0; i<(rowNumber+1)*xSize; i++, j++){
                //System.out.println(rowNumber);

                row[j]=array[i];
            }
            return row;
        }
    }

    public void setRow(int rowNumber, double[] rowValue){
        for(int i=rowNumber*xSize, j=0; i<(rowNumber+1)*xSize; i++, j++){
            array[i]=rowValue[j];
        }

    }
}



