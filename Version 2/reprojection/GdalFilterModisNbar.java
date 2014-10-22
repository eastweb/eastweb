package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;

import org.gdal.gdal.Dataset;

public class GdalFilterModisNbar extends GdalFilterModis {

    public GdalFilterModisNbar(File input, File output) {
        super(input, output);
    }

    @Override
    protected Dataset createOutput(Dataset inputDS) {
        Dataset outputDS = super.createOutput(inputDS);
        outputDS.GetRasterBand(1).SetNoDataValue(-32767);
        return outputDS;
    }

    @Override
    protected double filterValue(double value) {
        if (value < 0 || value > 32766) {
            return 32767;
        } else {
            return value;
        }
    }

}
