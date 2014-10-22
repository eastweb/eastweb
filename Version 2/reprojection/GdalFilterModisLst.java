package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;

import org.gdal.gdal.Dataset;

public class GdalFilterModisLst extends GdalFilterModis {

    public GdalFilterModisLst(File input, File output) {
        super(input, output);
    }

    @Override
    protected Dataset createOutput(Dataset inputDS) {
        Dataset outputDS = super.createOutput(inputDS);
        outputDS.GetRasterBand(1).SetNoDataValue(0);
        return outputDS;
    }

    @Override
    protected double filterValue(double value) {
        if (value < 7500) {
            return 32767;
        } else {
            return value * 0.02;
        }
    }

}
