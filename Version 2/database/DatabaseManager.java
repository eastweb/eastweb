package edu.sdstate.eastweb.prototype.database;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.ZonalStatistic;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;

/**
 * The database manager interface provides functionality for querying and
 * uploading and downloading raster and vector data from a GIS database. The
 * purpose of the database manager interface is to hide the choice of spatially
 * enabled database. Ideally, this interface should enable the rest of the
 * EASTWeb system to be database agnostic.
 * 
 * @author Isaiah Snell-Feikema
 */
public interface DatabaseManager {

    // NOTE: this is a draft of the interface. More methods will likely be
    // added.

    /**
     * Uploads the specified feature to the database.
     * 
     * @param file the shapefile (*.shp) to upload
     * @param project the project to associate the shapefile with
     * @return the id of the feature in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadFeature(File file, String project) throws IOException, SQLException;

    /**
     * Uploads the specified modis tile to the database.
     * 
     * @param file the .hdf to upload
     * @param product the modis product
     * @param date the date the data was captured
     * @param downloaded the date the data was downloaded from the host
     * @param horz the horizontal tile number
     * @param vert the vertical tile number
     * @return the id of the Modis raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadModis(File file, ModisProduct product, DataDate date, DataDate updated, int horz, int vert) throws IOException, SQLException;

    /**
     * Uploads the specified ETo file to the database.
     * 
     * @param file the
     * @param date
     * @param updated
     * @return the id of the ETo raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadEto(File file, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified TRMM file to the database.
     * 
     * @param file
     * @param date
     * @param updated
     * @return the id of the TRMM raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadTrmm(File file, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified projected Modis product to the database.
     * 
     * @param file
     * @param project
     * @param product
     * @param date
     * @param updated
     * @return the id of the projected Modis raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadProjectedModis(File file, String project, ModisProduct product, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified projected ETo product to the database.
     * 
     * @param file
     * @param project
     * @param date
     * @param updated
     * @return the id of the projected ETo raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadProjectedEto(File file, String project, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the projected TRMM product to the database.
     * 
     * @param file
     * @param project
     * @param date
     * @param updated
     * @return the id of the TRMM raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadProjectedTrmm(File file, String project, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified ETa product to the database.
     * 
     * @param file
     * @param project
     * @param date
     * @param updated
     * @return the id of the ETa raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadEta(File file, String project, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified EVI product to the database.
     * 
     * @param file
     * @param project
     * @param date
     * @param updated
     * @return the id of the EVI raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadEvi(File file, String project, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified NDVI product to the database.
     * 
     * @param file
     * @param project
     * @param date
     * @param updated
     * @return the id of the NDVI raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadNdvi(File file, String project, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified NDWI5 product to the database.
     * 
     * @param file
     * @param project
     * @param date
     * @param updated
     * @return the id of the NDWI5 raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadNdwi5(File file, String project, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified NDWI6 product to the database.
     * 
     * @param file
     * @param project
     * @param date
     * @param updated
     * @return the id of the NDWI6 raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadNdwi6(File file, String project, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Uploads the specified SAVI product to the database.
     * 
     * @param file
     * @param project
     * @param date
     * @param updated
     * @return the id of the SAVI raster in the database
     * @throws IOException
     * @throws SQLException
     */
    int uploadSavi(File file, String project, DataDate date, DataDate updated) throws IOException, SQLException;

    /**
     * Downloads the specified feature and places it in the specified file in
     * the shapefile.
     * 
     * @param file the location to save the feature
     * @param id the id of the feature
     * @throws IOException
     * @throws SQLException
     */
    void downloadFeature(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified Modis tile from the database.
     * 
     * @param file
     * @param product
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadModis(File file, ModisProduct product, int id) throws IOException, SQLException;

    /**
     * Downloads the specified ETo raster from the database and saves it to the
     * specified file.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadEto(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified TRMM raster from the database and saves it to the
     * specified file.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadTrmm(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified projected Modis product from the database.
     * 
     * @param file
     * @param product
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadProjectedModis(File file, ModisProduct product, int id)  throws IOException, SQLException;

    /**
     * Downloads the specified projected ETo product from the database.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadProjectedEto(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified projected TRMM product from the database.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadProjectedTrmm(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified ETa product from the database.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadEta(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified EVI product from the database.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadEvi(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified NDVI product from the database.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadNdvi(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified NDWI5 product from the database.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadNdwi5(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified NDWI6 product from the database.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadNdwi6(File file, int id) throws IOException, SQLException;

    /**
     * Downloads the specified SAVI product from the database.
     * 
     * @param file
     * @param id
     * @throws IOException
     * @throws SQLException
     */
    void downloadSavi(File file, int id) throws IOException, SQLException;

    /**
     * Returns the id of the MODIS raster for the specified product and date.
     * 
     * @param product
     * @param date
     * @param horz
     * @param vert
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getModisId(ModisProduct product, DataDate date, int horz, int vert) throws SQLException;

    /**
     * Returns the id of the ETo for the specified date.
     * 
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getEtoId(DataDate date) throws SQLException;

    /**
     * Returns the id of the TRMM for the specified date.
     * 
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getTrmmId(DataDate date) throws SQLException;

    /**
     * Returns the id of the reprojected Modis raster for the specified
     * project, product, and date.
     * 
     * @param project
     * @param product
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getReprojectedModisId(String project, DataDate date) throws SQLException;

    /**
     * Returns the id of the reprojected ETo raster for the specified project
     * and date.
     * 
     * @param project
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getReprojectedEtoId(String project, DataDate date) throws SQLException;

    /**
     * Returns the id of the reprojected TRMM raster for the specified project
     * and date.
     * 
     * @param project
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getReprojectedTrmmId(String project, DataDate date) throws SQLException;

    /**
     * Returns the id of the ETa raster for the specified project and date.
     * 
     * @param project
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getEtaId(String project, DataDate date) throws SQLException;

    /**
     * Returns the id of the EVI raster for the specified project and date.
     * 
     * @param project
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getEviId(String project, DataDate date) throws SQLException;

    /**
     * Returns the id of the NDVI raster for the specified project and date.
     * 
     * @param project
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getNdviId(String project, String feature, DataDate date) throws SQLException;

    /**
     * Returns the id of the NDWI5 raster for the specified project and date.
     * 
     * @param project
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getNdwi5Id(String project, String feature, DataDate date) throws SQLException;

    /**
     * Returns the id of the NDWI6 raster for the specified project and date.
     * 
     * @param project
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getNdwi6Id(String project, String feature, DataDate date) throws SQLException;

    /**
     * Returns the id of the SAVI raster for the specified project and date.
     * 
     * @param project
     * @param date
     * @return the id of the raster or -1 if the raster isn't found
     * @throws SQLException
     */
    int getSaviId(String project, String feature, DataDate date) throws SQLException;

    /**
     * Uploads the specified zonal statistics.
     * 
     * @param project
     * @param feature
     * @param zoneField
     * @param zone
     * @param date
     * @param index
     * @param zonalStatistics
     */
    void uploadZonalStatistic(String project, String feature, String zoneField,
            String zone, DataDate date, EnvironmentalIndex index,
            ZonalStatistic zonalStatistics) throws SQLException;

    /**
     * Returns the id of the zonal statistics for the specified parameters.
     * 
     * @param project
     * @param feature
     * @param zoneField
     * @param zone
     * @param date
     * @param index
     * @return the id or -1 if it is not found
     */
    int getZonalStatisticId(String project, String feature, String zoneField,
            String zone, DataDate date, EnvironmentalIndex index)
                    throws SQLException;

    /**
     * Returns zonal statistics for the specified zonal statistic id.
     * 
     * @param id
     * @return zonal statistics
     * @throws SQLException
     */
    ZonalStatistic getZonalStatistic(int id) throws SQLException;
}

