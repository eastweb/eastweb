package edu.sdstate.eastweb.prototype;

import java.io.File;

import edu.sdstate.eastweb.prototype.download.EtoArchive;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;

public final class DirectoryLayout {
    private DirectoryLayout() {
    }
    /**
     * TODO: This may be duplicated logic! Check Isaiah's code later!
     * @param project
     * @return
     * @throws ConfigReadException
     */
    private static String getProjectDirectoryName(ProjectInfo project) throws ConfigReadException {
        /*
        final String name = project.getName();
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < name.length(); ) {
            final int codepointIn = name.codePointAt(i);
            i += Character.charCount(codepointIn);

            final int codepointOut;
            if ((codepointIn >= 'a' && codepointIn <= 'z') ||
                    (codepointIn >= '0' && codepointIn <= '9')) {
                // Keep it
                codepointOut = codepointIn;
            } else if (codepointIn >= 'A' && codepointIn <= 'Z') {
                // Convert to lower-case
                codepointOut = codepointIn - 'A' + 'a';
            } else {
                // Convert to underscore
                codepointOut = '_';
            }

            builder.appendCodePoint(codepointOut);
        }

        return builder.toString();
         */

        return Config.getInstance().normalizeName(project.getName());
    }

    private static String getRootDirectory() throws ConfigReadException {
        return Config.getInstance().getRootDirectory();
    }

    private static File getFeature(ProjectInfo project, String feature) throws ConfigReadException{
        return new File(String.format(
                "%s/%s/%s.shp",
                getSettingsDirectory(project).getPath(),
                feature,
                feature
        ));
    }


    private static String getTrmmName(TrmmProduct product) {
        switch (product) {
        case TRMM_3B42:
            return "trmm";
        case TRMM_3B42RT:
            return "trmmrt";
        default:
            throw new IllegalArgumentException("Product " + product + " not supported.");
        }
    }

    private static String getModisDirectoryName(ModisProduct product) {
        switch (product) {
        case NBAR:
            return "modis-nbar";

        case LST:
            return "modis-lst";

        default:
            throw new IllegalArgumentException();
        }
    }

    private static String getIndexDirectoryName(EnvironmentalIndex index) {
        switch (index) {
        case LST_DAY:
            return "lstday";

        case LST_NIGHT:
            return "lstnight";

        case LST_MEAN:
            return "lst";

        case NDVI:
            return "ndvi";

        case EVI:
            return "evi";

        case NDWI5:
            return "ndwi5";

        case NDWI6:
            return "ndwi6";

        case SAVI:
            return "savi";

        case ETA:
            return "eta";

        case TRMM:
            return "trmm";

        case TRMM_RT:
            return "trmmrt";

        default:
            throw new IllegalArgumentException();
        }
    }

    private static String getIndexFileName(EnvironmentalIndex index) {
        switch (index) {
        case LST_DAY:
            return "day";

        case LST_NIGHT:
            return "night";

        case LST_MEAN:
            return "mean";

        default:
            return getIndexDirectoryName(index);
        }
    }

    public static File getSettingsDirectory(ProjectInfo project) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/settings",
                getRootDirectory(),
                getProjectDirectoryName(project)
        ));
    }

    public static File getModisDateCache(ModisProduct product) throws ConfigReadException {
        return new File(String.format(
                "%s/download/%s/DateCache.xml.gz",
                getRootDirectory(),
                getModisDirectoryName(product)
        ));
    }

    public static File getTrmmDateCache(TrmmProduct product) throws ConfigReadException {
        return new File(String.format(
                "%s/download/%s/DateCache.xml.gz",
                getRootDirectory(),
                getTrmmName(product)
        ));
    }

    public static File getEtoArchiveCache() throws ConfigReadException {
        return new File(String.format(
                "%s/download/eto/EtoArchiveCache.xml.gz",
                getRootDirectory()
        ));
    }

    public static File getModisTileCache(ModisProduct product, DataDate date)
    throws ConfigReadException
    {
        return new File(String.format(
                "%s/download/%s/%04d/%03d/ModisTileCache.xml.gz",
                getRootDirectory(),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getModisDownload(ModisProduct product, DataDate date, ModisTile tile)
    throws ConfigReadException
    {
        return new File(String.format(
                "%s/download/%s/%04d/%03d/h%02dv%02d/tile.hdf",
                getRootDirectory(),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear(),
                tile.getHTile(),
                tile.getVTile()
        ));
    }

    public static File getModisDownloadMetadata(ModisProduct product, DataDate date,
            ModisTile tile) throws ConfigReadException {
        return new File(String.format(
                "%s/download/%s/%04d/%03d/h%02dv%02d/ModisDownloadMetadata.xml.gz",
                getRootDirectory(),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear(),
                tile.getHTile(),
                tile.getVTile()
        ));
    }

    public static File getTrmmDownload(TrmmProduct product, DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/download/%s/%04d/%03d/%s.bin",
                getRootDirectory(),
                getTrmmName(product),
                date.getYear(),
                date.getDayOfYear(),
                getTrmmName(product)
        ));
    }

    public static File getTrmmDownloadMetadata(TrmmProduct product, DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/download/%s/%04d/%03d/TrmmDownloadMetadata.xml.gz",
                getRootDirectory(),
                getTrmmName(product),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getEtoDownloadDir(DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/download/eto/%04d/%03d",
                getRootDirectory(),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getEtoDownloadMainFile(DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/download/eto/%04d/%03d/et%02d%02d%02d.bil",
                getRootDirectory(),
                date.getYear(),
                date.getDayOfYear(),
                date.getYear() % 100,
                date.getMonth(),
                date.getDay()
        ));
    }

    public static File getEtoArchiveMetadata(EtoArchive archive) throws ConfigReadException {
        switch (archive.getType()) {
        case Yearly:
            return new File(String.format(
                    "%s/download/eto/%04d/EtoArchiveMetadata.yearly.xml.gz",
                    getRootDirectory(),
                    archive.getYear()
            ));

        case Monthly:
            return new File(String.format(
                    "%s/download/eto/%04d/EtoArchiveMetadata.monthly.%02d.xml.gz",
                    getRootDirectory(),
                    archive.getYear(),
                    archive.getMonth()
            ));

        case Daily:
            return new File(String.format(
                    "%s/download/eto/%04d/%03d/EtoArchiveMetadata.daily.xml.gz",
                    getRootDirectory(),
                    archive.getYear(),
                    archive.toDataDate().getDayOfYear()
            ));

        default:
            throw new IllegalArgumentException();
        }
    }

    public static File getEtoDownloadMetadata(DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/download/eto/%04d/%03d/EtoDownloadMetadata.xml.gz",
                getRootDirectory(),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getModisReprojectedFolder(ProjectInfo project, ModisProduct product,
            DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getModisReprojected(ProjectInfo project, ModisProduct product,
            DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/tmp.tif",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getModisReprojectedMetadata(ProjectInfo project, ModisProduct product,
            DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/ModisReprojectedMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getModisReprojectedBand(ProjectInfo project, ModisProduct product,
            DataDate date, String band) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/%s.tif",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear(),
                band
        ));
    }

    public static File getTrmmReprojectedFolder(ProjectInfo project,
            TrmmProduct product, DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getTrmmName(product),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getTrmmReprojected(ProjectInfo project, TrmmProduct product, DataDate date)
    throws ConfigReadException
    {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/%s.tif",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getTrmmName(product),
                date.getYear(),
                date.getDayOfYear(),
                getTrmmName(product)
        ));
    }

    public static File getTrmmReprojectedMetadata(ProjectInfo project, TrmmProduct product, DataDate date)
    throws ConfigReadException
    {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/TrmmReprojectedMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getTrmmName(product),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getEtoReprojectedFolder(ProjectInfo project, DataDate date)
    throws ConfigReadException
    {
        return new File(String.format(
                "%s/projects/%s/reprojected/eto/%04d/%03d",
                getRootDirectory(),
                getProjectDirectoryName(project),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getEtoReprojected(ProjectInfo project, DataDate date)
    throws ConfigReadException
    {
        return new File(String.format(
                "%s/eto.tif",
                getEtoReprojectedFolder(project, date).toString())
        );
    }

    public static File getEtoReprojectedMetadata(ProjectInfo project, DataDate date)
    throws ConfigReadException
    {
        return new File(String.format(
                "%s/projects/%s/reprojected/eto/%04d/%03d/EtoReprojectedMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(project),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getEtoClip(ProjectInfo project, DataDate date,
            String shapefile) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/indices/clip/eto/%04d/%03d/%s.tif",
                getRootDirectory(),
                getProjectDirectoryName(project),
                date.getYear(),
                date.getDayOfYear(),
                shapefile
        ));
    }

    public static File getTrmmClip(ProjectInfo project, TrmmProduct product, DataDate date,
            String shapefile) throws ConfigReadException {

        return new File(String.format(
                "%s/projects/%s/indices/clip/%s/%04d/%03d/%s/%s.tif",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getTrmmName(product),
                date.getYear(),
                date.getDayOfYear(),
                shapefile,
                getTrmmName(product)
        ));
    }

    public static File getModisClipFolder(ProjectInfo project, DataDate date,
            ModisProduct product, String shapefile) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/indices/clip/%s/%04d/%03d/%s/",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear(),
                shapefile
        ));
    }

    public static File getModisClip(ProjectInfo project, DataDate date,
            ModisProduct product, String shapefile, String band) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/indices/clip/%s/%04d/%03d/%s/%s.tif",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getModisDirectoryName(product),
                date.getYear(),
                date.getDayOfYear(),
                shapefile,
                band
        ));
    }

    public static File getIndexFolder(ProjectInfo project, EnvironmentalIndex index,
            DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/indices/%s/%04d/%03d",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getIndexDirectoryName(index),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getIndex(ProjectInfo project, EnvironmentalIndex index,
            DataDate date, String shapefile) throws ConfigReadException {
        String shapeFile = new File(shapefile).getName();
        return new File(String.format(
                "%s/projects/%s/indices/%s/%04d/%03d/%s/%s.tif",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getIndexDirectoryName(index),
                date.getYear(),
                date.getDayOfYear(),
                shapeFile.substring(0, shapeFile.indexOf('.')),
                getIndexFileName(index)
        ));
    }

    public static File getIndexMetadata(ProjectInfo project, EnvironmentalIndex index,
            DataDate date, String shapefile) throws ConfigReadException {
        String shapeFile = new File(shapefile).getName();
        return new File(String.format(
                "%s/projects/%s/indices/%s/%04d/%03d/%s/IndexMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getIndexDirectoryName(index),
                date.getYear(),
                date.getDayOfYear(),
                shapeFile.substring(0, shapeFile.indexOf('.'))
        ));
    }

    public static File getZonalSummaryFolder(ProjectInfo project, EnvironmentalIndex index,
            DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/zonal/%s/%04d/%03d",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getIndexDirectoryName(index),
                date.getYear(),
                date.getDayOfYear()
        ));
    }

    public static File getZonalSummary(ProjectInfo project, EnvironmentalIndex index,
            DataDate date, String zonalSummary) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/zonal/%s/%04d/%03d/%s/%s/zonal.csv",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getIndexDirectoryName(index),
                date.getYear(),
                date.getDayOfYear(),
                zonalSummary,
                getIndexFileName(index)
        ));
    }

    public static File getZonalSummaryMetadata(ProjectInfo project, EnvironmentalIndex index,
            DataDate date, String zonalSummary) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/zonal/%s/%04d/%03d/%s/%s/ZonalSummaryMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getIndexDirectoryName(index),
                date.getYear(),
                date.getDayOfYear(),
                zonalSummary,
                getIndexFileName(index)
        ));
    }

    public static File getDatabaseInsertMetadata(ProjectInfo project, EnvironmentalIndex index,
            DataDate date, String zonalSummary) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/zonal/%s/%04d/%03d/%s/%s/ResultUploadMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(project),
                getIndexDirectoryName(index),
                date.getYear(),
                date.getDayOfYear(),
                zonalSummary,
                getIndexFileName(index)
        ));
    }
}