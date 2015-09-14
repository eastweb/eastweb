package edu.sdstate.eastweb.prototype.zonalstatistics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

/**
 * 
 * @author Isaiah Snell-Feikema
 *
 */
public class GdalZonalStatistics {

    private final File mRasterFile;
    private final File mLayerFile;
    private final String mField;
    private final File mTableFile;

    public static void main(String[] args) throws Exception {
        GdalZonalStatistics zonalStatistics = new GdalZonalStatistics(
                new File(args[0]),
                new File(args[1]),
                args[2],
                new File(args[3])
        );

        zonalStatistics.calculate();
    }

    /**
     * 
     * @param raster
     * @param layer must be a subset of raster
     * @param field must be an integer field that exists within layer
     * @param table
     * @throws Exception
     */
    public GdalZonalStatistics(File raster, File layer, String field, File table) {
        mRasterFile = raster;
        mLayerFile = layer;
        mField = field;
        mTableFile = table;
    }


    /**
     * 
     * 
     * @throws Exception
     */
    public void calculate() throws Exception {
        GdalUtils.register();

        synchronized (GdalUtils.lockObject) {
            Dataset raster = null;
            DataSource layerSource = null;
            Layer layer = null;
            Dataset zoneRaster = null;
            try {
                Map<Integer, Long> countMap = new HashMap<Integer, Long>();
                Map<Integer, Double> sumMap = new HashMap<Integer, Double>();
                Map<Integer, Double> minMap = new HashMap<Integer, Double>();
                Map<Integer, Double> maxMap = new HashMap<Integer, Double>();
                Map<Integer, Double> meanMap = new HashMap<Integer, Double>();
                Map<Integer, Double> stdMap = new HashMap<Integer, Double>();

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
                calculateStatistics(
                        raster, zoneRaster, layer,
                        countMap, sumMap,
                        minMap, maxMap,
                        meanMap, stdMap
                );

                // Write the table
                writeTable(
                        layer,
                        countMap, sumMap,
                        minMap, maxMap,
                        meanMap, stdMap
                );
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


    private void calculateStatistics(
            Dataset rasterDS, Dataset featureDS, Layer layer,
            Map<Integer, Long> countMap, Map<Integer, Double> sumMap,
            Map<Integer, Double> minMap, Map<Integer, Double> maxMap,
            Map<Integer, Double> meanMap, Map<Integer, Double> stdMap) throws Exception {

        Map<Integer, Double> squareSumMap = new HashMap<Integer, Double>();

        // Calculate zonal statistics
        Band zoneBand = featureDS.GetRasterBand(1); GdalUtils.errorCheck();
        Band rasterBand = rasterDS.GetRasterBand(1); GdalUtils.errorCheck();

        final int WIDTH = zoneBand.GetXSize(); GdalUtils.errorCheck();
        final int HEIGHT = zoneBand.GetYSize(); GdalUtils.errorCheck();

        int[] zoneArray = new int[WIDTH];
        double[] rasterArray = new double[WIDTH];

        Double[] noData = new Double[1];
        rasterBand.GetNoDataValue(noData);
        final float NO_DATA = noData[0].floatValue();

        for (int y=0; y<HEIGHT; y++) {
            zoneBand.ReadRaster(0, y, WIDTH, 1, zoneArray); GdalUtils.errorCheck();
            rasterBand.ReadRaster(0, y, WIDTH, 1, rasterArray); GdalUtils.errorCheck();

            for (int i=0; i<WIDTH; i++) {
                int zone = zoneArray[i];
                double value = rasterArray[i];
                if (zone != 0 && value != NO_DATA) { // Neither are no data values
                    if (countMap.get(zone) == null) { // New zone
                        countMap.put(zone, 1l);
                        sumMap.put(zone, value);
                        squareSumMap.put(zone, value*value);
                        minMap.put(zone, value);
                        maxMap.put(zone, value);
                    } else {
                        countMap.put(zone, countMap.get(zone) + 1);
                        sumMap.put(zone, sumMap.get(zone) + value);
                        squareSumMap.put(zone, squareSumMap.get(zone) + value*value);

                        if (minMap.get(zone) > value) {
                            minMap.put(zone, value);
                        }

                        if (maxMap.get(zone) < value) {
                            maxMap.put(zone, value);
                        }
                    }
                }
            }
        }

        System.out.print("Count: ");
        System.out.println(countMap);
        System.out.format("Sum: %s\n", sumMap.toString());
        System.out.format("Square Sum: %s\n", squareSumMap.toString());
        System.out.print("Min: ");
        System.out.println(minMap);
        System.out.print("Max: ");
        System.out.println(maxMap);

        layer.ResetReading();
        Feature feature = layer.GetNextFeature(); GdalUtils.errorCheck();
        while (feature != null) {
            int zone = feature.GetFieldAsInteger(mField); GdalUtils.errorCheck();
            if (countMap.get(zone) != null) {
                meanMap.put(zone, sumMap.get(zone)/countMap.get(zone));
                stdMap.put(zone, Math.sqrt( (squareSumMap.get(zone)/countMap.get(zone)) - (meanMap.get(zone) * meanMap.get(zone)) ));
            }
            feature = layer.GetNextFeature(); GdalUtils.errorCheck();
        }

        System.out.format("Mean: %s\n", meanMap.toString());
        System.out.format("Standard deviation: %s\n", stdMap.toString());
    }


    private void writeTable(
            Layer layer,
            Map<Integer, Long> countMap, Map<Integer, Double> sumMap,
            Map<Integer, Double> minMap, Map<Integer, Double> maxMap,
            Map<Integer, Double> meanMap, Map<Integer, Double> stdMap
    ) throws Exception {

        // Every map is expected to be the same size
        assert(countMap.size() == sumMap.size());
        assert(countMap.size() == minMap.size());
        assert(countMap.size() == maxMap.size());
        assert(countMap.size() == meanMap.size());
        assert(countMap.size() == stdMap.size());

        // Write the table
        PrintWriter writer = new PrintWriter(mTableFile);

        layer.ResetReading(); GdalUtils.errorCheck();
        Feature feature = layer.GetNextFeature(); GdalUtils.errorCheck();
        while (feature != null) {
            int zone = feature.GetFieldAsInteger(mField); GdalUtils.errorCheck();
            if (countMap.get(zone) != null && countMap.get(zone) != 0) {
                writer.print(zone + ",");
                writer.print(countMap.get(zone) + ",");
                writer.print(sumMap.get(zone) + ",");
                writer.print(meanMap.get(zone) + ",");
                writer.print(stdMap.get(zone));
                writer.println();
            }
            feature = layer.GetNextFeature(); GdalUtils.errorCheck();
        }

        writer.close();
    }

}
