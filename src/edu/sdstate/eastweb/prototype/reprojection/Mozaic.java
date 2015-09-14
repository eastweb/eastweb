package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

/**
 * Mozaic several tile files into one file for specific band numbers.
 * 
 * @author Jiameng Hu
 */
public class Mozaic {
    int xSize;
    int ySize;
    int outputXSize;
    int outputYSize;
    int tileNumber;
    ModisTileData[] tileList;
    ModisTileData[][] tileMetrix;
    int tileMetrixRow;
    int tileMetrixClo;
    int[] band;
    ArrayList<File> outputFiles;
    private File tempDictionary;

    public Mozaic(File[] input, int[] band) throws InterruptedException,
    ConfigReadException {

        tempDictionary = new File(Config.getInstance().getTempDirectory());
        this.band = band.clone();
        outputFiles = new ArrayList<File>();
        // read tile data and get size of tiles
        tileNumber = input.length;
        tileList = new ModisTileData[tileNumber];
        for (int i = 0; i < tileNumber; i++) {
            tileList[i] = new ModisTileData(input[i]);
        }
        xSize = tileList[0].xSize;
        ySize = tileList[0].ySize;

    }

    public ArrayList<File> run() throws IOException {
        synchronized (GdalUtils.lockObject) {
            // sort tiles
            sortTiles();
            // link tiles
            linkTiles();
            return outputFiles;
        }
    }

    void sortTiles() {
        int minH = tileList[0].horizon;
        int maxH = tileList[0].horizon;
        int minV = tileList[0].vertical;
        int maxV = tileList[0].vertical;
        for (int i = 0; i < tileNumber; i++) {
            if (minH > tileList[i].horizon) {
                minH = tileList[i].horizon;
            }

            if (maxH < tileList[i].horizon) {
                maxH = tileList[i].horizon;
            }
            if (minV > tileList[i].vertical) {
                minV = tileList[i].vertical;
            }

            if (maxV < tileList[i].vertical) {
                maxV = tileList[i].vertical;
            }
        }
        tileMetrixRow = maxV - minV + 1;
        tileMetrixClo = maxH - minH + 1;
        // System.out.println(tileMetrixRow);
        // System.out.println(tileMetrixClo);
        tileMetrix = new ModisTileData[tileMetrixRow][tileMetrixClo];

        for (int i = 0; i < tileNumber; i++) {
            // System.out.println(tileList[i].vertical);
            // System.out.println(tileList[i].horizon);
            tileMetrix[tileList[i].vertical - minV][tileList[i].horizon - minH] =
                tileList[i];
        }

        for (int i = 0; i < tileMetrixRow; i++) {
            for (int j = 0; j < tileMetrixClo; j++) {
                if (tileMetrix[i][j] != null) {
                    System.out.println("true" + i + " " + j);
                } else {
                    System.out.println("false" + i + " " + j);
                }

            }
        }
        outputXSize = xSize * tileMetrixClo;
        outputYSize = ySize * tileMetrixRow;
    }

    // calculate the row number of tilemetrix[][], for the giving line
    // number(row) of output
    // Assuming tile=1200*1200: return 0 if 0<=line<1200;
    // return 1 if 1200<=line<2400;
    private int getRow(int line) {
        int row = 0;
        for (int i = 0; i < tileMetrixRow; i++) {

            if (line / ((i + 1) * xSize) == 0) {
                row = i;
                return row;
            }
        }
        return row;
    }

    private void linkTiles() throws IOException {

        System.out.println("enter link");
        // loop for each band needed be reprojected
        for (int i = 0; i < band.length; i++) {
            int currentBand = band[i];
            File temp =
                File.createTempFile("band" + currentBand, ".tif",
                        tempDictionary);
            System.out.println("create temp: " + temp.toString());
            temp.deleteOnExit();

            // System.out.println("before creat output");
            // create output file and set the metadata
            String[] option = { "INTERLEAVE=PIXEL" };
            Dataset output =
                gdal.GetDriverByName("GTiff").Create(
                        temp.getAbsolutePath(), outputXSize, outputYSize,
                        1, // band number
                        gdalconst.GDT_Float32, option);

            Dataset input = gdal.Open(tileList[0].sdsName[0]);
            output.SetGeoTransform(input.GetGeoTransform());
            output.SetProjection(input.GetProjection());
            output.SetMetadata(input.GetMetadata_Dict());
            // if error happens, change to input=null
            input.delete();

            // outputTemp is used to store double array data of output file
            ImageArray outputTemp =
                new ImageArray(output.getRasterXSize(),
                        output.getRasterYSize());

            // loop for each tile
            for (int col = 0; col < tileMetrixClo; col++) {
                for (int row = 0; row < tileMetrixRow; row++) {
                    ImageArray tempArray;
                    if (tileMetrix[row][col] != null) {
                        System.out
                        .println("current= "
                                + currentBand
                                + " "
                                + tileMetrix[row][col].sdsName[currentBand - 1]);
                        // currentTile=null;
                        Dataset tempTile =
                            gdal.Open(tileMetrix[row][col].sdsName[currentBand - 1]);
                        tempArray = new ImageArray(tempTile.GetRasterBand(1));
                        tempTile.delete();

                    } else {
                        tempArray = null;
                    }

                    // loop for each row of temp array image
                    for (int j = ySize * row; j < ySize * (row + 1); j++) {
                        double[] rowTemp = outputTemp.getRow(j);
                        if (tempArray != null) {
                            double[] tileRow =
                                tempArray.getRow(j - row * ySize);
                            System.arraycopy(tileRow, 0, rowTemp, col * xSize,
                                    xSize);
                        } else {
                            // set value for the no tile data area
                            double[] tileRow = new double[xSize];
                            for (int k = 0; k < xSize; k++) {
                                tileRow[k] = -3.40282346639e+038;
                            }
                            System.arraycopy(tileRow, 0, rowTemp, col * xSize,
                                    xSize);
                        }

                        outputTemp.setRow(j, rowTemp);
                        rowTemp = null;

                    }

                    // destroy tempArray
                    tempArray = null;
                    // tileMetrix[row][col]=null;

                }
            }
            output.GetRasterBand(1).WriteRaster(0, 0, output.getRasterXSize(),
                    output.getRasterYSize(), outputTemp.getArray());

            output.GetRasterBand(1).ComputeStatistics(true);
            output.delete();
            outputTemp=null;

            // add this band mozaic product into outputFile arraylist
            outputFiles.add(temp);
        }

    }

}