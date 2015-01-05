package version2.prototype;

import java.io.File;

import version2.prototype.Config;
import version2.prototype.ConfigReadException;
import version2.prototype.DataDate;
import version2.prototype.ProjectInfo;

public final class DirectoryLayout {
    private DirectoryLayout() {
    }
    /**
     * TODO: This may be duplicated logic! Check Isaiah's code later!
     * @param project
     * @return
     * @throws ConfigReadException
     */
    public static String getProjectDirectoryName(ProjectInfo project) throws ConfigReadException {
        return Config.getInstance().normalizeName(project.getName());
    }

    public static String getRootDirectory() throws ConfigReadException {
        return Config.getInstance().getRootDirectory();
    }

    public static File getSettingsDirectory(ProjectInfo project) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/settings",
                getRootDirectory(),
                getProjectDirectoryName(project)
                ));
    }


    public static File getDateCache(String pluginName) throws ConfigReadException {
        return new File(String.format(
                "%s/download/%s/DateCache.xml.gz",
                getRootDirectory(),
                pluginName
                ));
    }

    public static File getDownloadMetadata(String pluginName, DataDate date)throws ConfigReadException{
        return new File(String.format(
                "%s/download/%s/%04d/%03d/"+pluginName+"DownloadMetadata.xml.gz",
                getRootDirectory(),
                pluginName,
                date.getYear(),
                date.getDayOfYear()
                ));
    }

    public static File getNldasReprojectedMetadata(ProjectInfo project, DataDate date)
            throws ConfigReadException
    {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/NldasReprojectedMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(project),
                "Nldas",
                date.getYear(),
                date.getDayOfYear()
                ));
    }

    public static File getIndexMetadata(ProjectInfo project, String index,
            DataDate date, String shapefile) throws ConfigReadException {
        String shapeFile = new File(shapefile).getName();
        return new File(String.format(
                "%s/projects/%s/indices/%s/%04d/%03d/%s/IndexMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(project),
                index,
                date.getYear(),
                date.getDayOfYear(),
                shapeFile.substring(0, shapeFile.indexOf('.'))
                ));
    }
    public static File getNldasDownload(DataDate date) throws ConfigReadException {
        return new File(String.format(
                "%s/download/%s/%04d/%03d/%02d/%s.gbr",
                getRootDirectory(),
                "Nldas",
                date.getYear(),
                date.getDayOfYear(),
                date.getHour(),
                "Nldas"
                ));
    }
    public static File getModisReprojectedMetadata(ProjectInfo mProject,
            String mProduct, DataDate mDate) throws ConfigReadException {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/ModisReprojectedMetadata.xml.gz",
                getRootDirectory(),
                getProjectDirectoryName(mProject),
                getModisDirectoryName(mProduct),
                mDate.getYear(),
                mDate.getDayOfYear()
                ));
    }
    public static File getModisDownload(String mProduct, DataDate mDate,
            ModisTile tile) throws ConfigReadException {
        return new File(String.format(
                "%s/download/%s/%04d/%03d/h%02dv%02d/tile.hdf",
                getRootDirectory(),
                getModisDirectoryName(mProduct),
                mDate.getYear(),
                mDate.getDayOfYear(),
                tile.getHTile(),
                tile.getVTile()
                ));
    }

    public static File getNldasReprojected(ProjectInfo project, DataDate date)
            throws ConfigReadException
    {
        return new File(String.format(
                "%s/projects/%s/reprojected/%s/%04d/%03d/%s.tif",
                getRootDirectory(),
                getProjectDirectoryName(project),
                "Nldas",
                date.getYear(),
                date.getDayOfYear(),
                "Nldas"
                ));
    }

    public static String getModisDirectoryName(String mProduct) {

        if(mProduct == "NBAR")
        {
            return "modis-nbar";
        }
        else if (mProduct == "LST")
        {
            return "modis-lst";
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}