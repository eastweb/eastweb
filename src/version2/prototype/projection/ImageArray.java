package version2.prototype.projection;

import org.gdal.gdal.Band;

import version2.prototype.util.GdalUtils;

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

            band.ReadRaster(0, 0, xSize, ySize, array);
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