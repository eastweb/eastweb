package version2.prototype.summary;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Transformer;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;

import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class ZonalSummaryCalculator implements SummaryCalculator {

    private File mRasterFile;
    private File mLayerFile;
    private File mTableFile;
    private String mField;
    private SummariesCollection summaries;

    /**
     * @param inRaster
     * @param inShape
     * @param outTable
     * @param zone
     * @param sumStrategy
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */

    public ZonalSummaryCalculator(SummaryData data)
    {

    }

    public ZonalSummaryCalculator(File inRaster, File inShape, File outTable,
            String zone, String... summarySingletonNames) throws Exception {
        super();
        mRasterFile = inRaster;
        mLayerFile = inShape;
        mTableFile = outTable;
        mField = zone;
        summaries = new SummariesCollection(summarySingletonNames);
    }

    @Override
    public void calculate() throws Exception {
        GdalUtils.register();

        synchronized (GdalUtils.lockObject) {
            Dataset raster = null;
            DataSource layerSource = null;
            Layer layer = null;
            Dataset zoneRaster = null;
            try {
                // Open inputs
                raster = gdal.Open(mRasterFile.getPath()); GdalUtils.errorCheck();
                layerSource = ogr.Open(mLayerFile.getPath());
                if (layerSource == null) {
                    throw new IOException("Could not load " + mLayerFile.getPath());
                }
                layer = layerSource.GetLayer(0);
                if (layer == null) {
                    throw new IOException("Could not load layer 0 of " + mLayerFile.getPath());
                }

                // Validate inputs
                if (!isSameProjection(raster, layer)) {
                    throw new IOException("\"" + mRasterFile.getPath() + "\" isn't in same projection as \"" + mLayerFile.getPath() + "\"");
                }

                if (!isLayerSubsetOfRaster(layer, raster)) {
                    throw new IOException("\"" + mLayerFile.getPath() + "\" isn't a subset of \"" + mRasterFile.getPath() + "\".");
                }

                // Create the zone raster
                zoneRaster = rasterize(layer, raster.GetGeoTransform());

                assert(raster.GetRasterXSize() == zoneRaster.GetRasterXSize());
                assert(raster.GetRasterYSize() == zoneRaster.GetRasterYSize());

                // Calculate statistics
                calculateStatistics(raster, zoneRaster, layer);

                // Write the table
                writeTable(layer);
            } finally { // Clean up
                if (raster != null) {
                    raster.delete(); GdalUtils.errorCheck();
                }
                if (layer != null) {
                    layer.delete(); GdalUtils.errorCheck();
                }
                if (layerSource != null) {
                    layerSource.delete(); GdalUtils.errorCheck();
                }
                if (zoneRaster != null) {
                    zoneRaster.delete(); GdalUtils.errorCheck();
                }
            }
        }
    }

    /**
     * Checks whether the given raster and layer share the same projection.
     * 
     * @param raster
     * @param layer
     * @return true if projections are the same
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws IllegalArgumentException
     */
    private boolean isSameProjection(Dataset raster, Layer layer) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        SpatialReference rasterRef = new SpatialReference(raster.GetProjection()); GdalUtils.errorCheck();
        boolean same = layer.GetSpatialRef().IsSame(rasterRef) != 0; GdalUtils.errorCheck();
        return same;
    }


    private boolean isLayerSubsetOfRaster(Layer layer, Dataset raster)
            throws IllegalArgumentException, UnsupportedOperationException, IOException {
        double[] extent = layer.GetExtent(true); GdalUtils.errorCheck();

        Vector<String> options = new Vector<String>();
        options.add("SRC_DS=" + layer.GetSpatialRef().ExportToWkt()); GdalUtils.errorCheck();

        Transformer transformer = new Transformer(null, raster, options); GdalUtils.errorCheck();

        double[] min = new double[] {Math.min(extent[0], extent[1]), Math.min(extent[2], extent[3]), 0};
        double[] max = new double[] {Math.max(extent[0], extent[1]), Math.max(extent[2], extent[3]), 0};

        transformer.TransformPoint(0, min); GdalUtils.errorCheck();
        transformer.TransformPoint(0, max); GdalUtils.errorCheck();

        int layerMinX = (int) Math.round(Math.min(min[0], max[0]));
        int layerMaxX = (int) Math.round(Math.max(min[0], max[0]));
        int layerMinY = (int) Math.round(Math.min(min[1], max[1]));
        int layerMaxY = (int) Math.round(Math.max(min[1], max[1]));

        System.out.format(
                "Layer extent: %d %d %d %d\n",
                layerMinX,
                layerMaxX,
                layerMinY,
                layerMaxY
                );


        System.out.format(
                "%d %d\n",
                raster.GetRasterXSize(),
                raster.GetRasterYSize()
                );

        int rasterMinX = 0;
        int rasterMaxX = raster.GetRasterXSize(); GdalUtils.errorCheck();
        int rasterMinY = 0;
        int rasterMaxY = raster.GetRasterYSize(); GdalUtils.errorCheck();

        if (layerMinX < rasterMinX) {
            return false;
        } else if (layerMaxX > rasterMaxX) {
            return false;
        } else if (layerMinY < rasterMinY) {
            return false;
        } else if (layerMaxY > rasterMaxY) {
            return false;
        }

        return true;
    }


    /**
     * 
     * 
     * @param layer
     * @param transform
     * @return
     * @throws Exception
     */
    private Dataset rasterize(Layer layer, double[] transform) throws Exception {

        // Create the raster to burn values into
        double[] layerExtent = layer.GetExtent(); GdalUtils.errorCheck();
        System.out.format("Feature extent: %s\n", Arrays.toString(layerExtent));
        System.out.println(Arrays.toString(transform));

        Dataset zoneRaster = gdal.GetDriverByName("MEM").Create(
                "",
                (int) Math.ceil((layerExtent[1]-layerExtent[0]) / Math.abs(transform[1])),
                (int) Math.ceil((layerExtent[3]-layerExtent[2]) / Math.abs(transform[5])),
                1,
                gdalconst.GDT_UInt32
                );
        GdalUtils.errorCheck();

        zoneRaster.SetProjection(layer.GetSpatialRef().ExportToWkt()); GdalUtils.errorCheck();
        zoneRaster.SetGeoTransform(new double[] {
                layerExtent[0], transform[1], 0,
                layerExtent[2] + zoneRaster.GetRasterYSize()*Math.abs(transform[5]), 0, transform[5]
        });
        GdalUtils.errorCheck();

        // Burn the values
        Vector<String> options = new Vector<String>();
        options.add("ATTRIBUTE=" + mField);

        gdal.RasterizeLayer(zoneRaster, new int[] {1}, layer, null, options); GdalUtils.errorCheck();


        return zoneRaster;
    }


    private void calculateStatistics(Dataset rasterDS, Dataset featureDS, Layer layer) throws Exception {
        // Calculate zonal statistics
        Band zoneBand = featureDS.GetRasterBand(1); GdalUtils.errorCheck();
        Band rasterBand = rasterDS.GetRasterBand(1); GdalUtils.errorCheck();

        final int WIDTH = zoneBand.GetXSize(); GdalUtils.errorCheck();
        final int HEIGHT = zoneBand.GetYSize(); GdalUtils.errorCheck();

        int[] zoneArray = new int[WIDTH];
        double[] rasterArray = new double[WIDTH];

        Double[] noData = new Double[1];
        //FIXME: Can't get the no data value from NLDAS reprojected file, manually set it to 0. It will affect the zonal result.
        rasterBand.GetNoDataValue(noData);
        //final float NO_DATA = noData[0].floatValue();
        final float NO_DATA=0;
        for (int y=0; y<HEIGHT; y++) {
            zoneBand.ReadRaster(0, y, WIDTH, 1, zoneArray); GdalUtils.errorCheck();
            rasterBand.ReadRaster(0, y, WIDTH, 1, rasterArray); GdalUtils.errorCheck();

            for (int i=0; i<WIDTH; i++) {
                int zone = zoneArray[i];
                double value = rasterArray[i];
                if (zone != 0 && value != NO_DATA) { // Neither are no data values
                    summaries.put(zone, value);
                }
            }
        }

        ArrayList<SummaryNameResultPair> results = summaries.getResults();
        for(SummaryNameResultPair pair : results){
            System.out.println(pair.toString());
        }
    }


    private void writeTable(Layer layer) throws Exception {
        // Write the table
        PrintWriter writer = new PrintWriter(mTableFile);

        layer.ResetReading(); GdalUtils.errorCheck();
        Feature feature = layer.GetNextFeature(); GdalUtils.errorCheck();
        ArrayList<SummaryNameResultPair> results = summaries.getResults();
        Map<Integer, Double> countMap = new HashMap<Integer, Double>(1);
        for(SummaryNameResultPair pair : results){
            if(pair.getSimpleName().equalsIgnoreCase("count")){
                countMap = pair.getResult();
                break;
            }
        }
        while (feature != null) {
            int zone = feature.GetFieldAsInteger(mField); GdalUtils.errorCheck();
            if (countMap.get(zone) != null && countMap.get(zone) != 0) {
                writer.print(zone + ",");
                for(int i=0; i < results.size(); i++){
                    if(i < results.size() - 1) {
                        System.out.println(results.get(i).toString(",") + ", ");
                    } else {
                        System.out.println(results.get(i).toString(","));
                    }
                }
                writer.println();
            }
            feature = layer.GetNextFeature(); GdalUtils.errorCheck();
        }

        writer.close();
    }
}
