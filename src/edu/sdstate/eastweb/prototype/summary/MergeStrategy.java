package edu.sdstate.eastweb.prototype.summary;

import java.io.File;

public interface MergeStrategy {

    File merge(File raster1, File raster2) throws Exception;
}
