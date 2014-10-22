package edu.sdstate.eastweb.prototype.zonalstatistics;

import java.io.File;

public interface ZonalStatistics {
    public abstract void computeZonalStats(File inRaster, File inShapefile,
            String field, File outTable) throws Exception;
}
