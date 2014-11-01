package version2.prototype.summary;

import java.io.File;

public interface InterpolateStrategy {

    File interpolate(File raster1, File raster2) throws Exception;
}
