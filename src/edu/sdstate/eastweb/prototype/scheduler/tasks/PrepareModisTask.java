package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ModisTile;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisDownloadMetadata;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.reprojection.GdalFilterModisLst;
import edu.sdstate.eastweb.prototype.reprojection.GdalFilterModisNbar;
import edu.sdstate.eastweb.prototype.reprojection.GdalFilterWithWatermask;
import edu.sdstate.eastweb.prototype.reprojection.ModisReprojectedMetadata;
import edu.sdstate.eastweb.prototype.reprojection.ModisReprojection;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

public class PrepareModisTask implements RunnableTask {
    private final ProjectInfo mProject;
    private final ModisProduct mProduct;
    private final DataDate mDate;

    public PrepareModisTask(ProjectInfo project, ModisProduct product, DataDate date) {
        mProject = project;
        mProduct = product;
        mDate = date;
        System.out.println("construct of prepareModisTask" +mProject.toString()+mProduct.toString()+mDate.toString());
    }

    private File getMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getModisReprojectedMetadata(mProject, mProduct, mDate);
    }

    private ModisReprojectedMetadata makeMetadata() throws IOException {
        final List<ModisDownloadMetadata> downloads = new ArrayList<ModisDownloadMetadata>();
        for (ModisTile tile : mProject.getModisTiles()) {
            downloads.add(ModisDownloadMetadata.fromFile(
                    DirectoryLayout.getModisDownloadMetadata(mProduct, mDate, tile)));
        }

        return new ModisReprojectedMetadata(downloads, new Date().getTime());
    }

    @Override
    public boolean getCanSkip() {
        try {
            return ModisReprojectedMetadata.fromFile(getMetadataFile()).equalsIgnoreTimestamp(makeMetadata());
        } catch (IOException e) {
            return false;
        }
    }

    private File[] getInputFiles() throws ConfigReadException {
        final List<File> files = new ArrayList<File>();

        for (ModisTile tile : mProject.getModisTiles()) {
            files.add(DirectoryLayout.getModisDownload(mProduct, mDate, tile));
        }
        return files.toArray(new File[0]);
    }

    private int[] getBands() {
        switch (mProduct) {
        case NBAR:
            return new int[] { 1,2, 3, 4, 5, 6, 7 };

        case LST:
            return new int[] { 1,5 };

        default:
            throw new IllegalArgumentException();
        }
    }

    private int getNumExpectedFiles() {
        switch (mProduct) {
        case NBAR:
            return 7;

        case LST:
            return 2;

        default:
            throw new IllegalArgumentException();
        }
    }



    private File getOutputFile() throws IOException, ConfigReadException {
        return DirectoryLayout.getModisReprojected(mProject, mProduct, mDate);
    }

    /**
     * FIXME: ugly
     */
    @Override
    public void run() throws Exception {
        try {
            // Prepare output directory
            final File outputFile = getOutputFile();
            FileUtils.forceMkdir(outputFile.getParentFile());
            switch (mProduct){
            case NBAR:

                ModisReprojection nbarProjection=new ModisReprojection();

                nbarProjection.project(getInputFiles(),
                        mProject,
                        new File[]{
                    new File(outputFile.getParent(), "projected.Nadir_Reflectance_Band1.tif"),
                    new File(outputFile.getParent(),"projected.Nadir_Reflectance_Band2.tif"),
                    new File(outputFile.getParent(),"projected.Nadir_Reflectance_Band3.tif"),
                    new File(outputFile.getParent(), "projected.Nadir_Reflectance_Band4.tif"),
                    new File(outputFile.getParent(), "projected.Nadir_Reflectance_Band5.tif"),
                    new File(outputFile.getParent(), "projected.Nadir_Reflectance_Band6.tif"),
                    new File(outputFile.getParent(),"projected.Nadir_Reflectance_Band7.tif"),
                },
                getBands());
                nbarProjection=null;
                break;
            case LST:
                ModisReprojection lstProjection=new ModisReprojection();
                lstProjection.project(
                        getInputFiles(),
                        mProject,
                        new File[]
                                 {
                            new File(outputFile.getParent(), "projected.LST_Day_1km.tif"),
                            new File(outputFile.getParent(), "projected.LST_Night_1km.tif"),
                                 },
                                 getBands()
                );
                lstProjection=null;
                break;
            }

            System.out.println("In prepareModis, finish project, going to filter");

            // Filter
            switch (mProduct) {
            case NBAR:
                for (int i=1; i<=7; i++) {
                    new GdalFilterModisNbar(
                            new File(outputFile.getParent(), "projected.Nadir_Reflectance_Band" + i + ".tif"),
                            new File(outputFile.getParent(), "filtered.Nadir_Reflectance_Band" + i + ".tif")
                    ).filter();
                    FileUtils.deleteQuietly(new File(outputFile.getParent(), "projected.Nadir_Reflectance_Band" + i + ".tif"));

                    new GdalFilterWithWatermask(
                            new File(outputFile.getParent(), "filtered.Nadir_Reflectance_Band" + i + ".tif"),
                            new File(DirectoryLayout.getSettingsDirectory(mProject), mProject.getWatermask()),
                            new File(outputFile.getParent(), "Nadir_Reflectance_Band" + i + ".tif")
                    ).filter();
                    FileUtils.deleteQuietly(new File(outputFile.getParent(), "filtered.Nadir_Reflectance_Band" + i + ".tif"));
                }
                break;
            case LST:
                new GdalFilterModisLst(
                        new File(outputFile.getParent(), "projected.LST_Day_1km.tif"),
                        new File(outputFile.getParent(), "filtered.LST_Day_1km.tif")
                ).filter();
                FileUtils.deleteQuietly(new File(outputFile.getParent(), "projected.LST_Day_1km.tif"));

                new GdalFilterModisLst(
                        new File(outputFile.getParent(), "projected.LST_Night_1km.tif"),
                        new File(outputFile.getParent(), "filtered.LST_Night_1km.tif")
                ).filter();
                FileUtils.deleteQuietly(new File(outputFile.getParent(), "projected.LST_Night_1km.tif"));

                new GdalFilterWithWatermask(
                        new File(outputFile.getParent(), "filtered.LST_Day_1km.tif"),
                        new File(DirectoryLayout.getSettingsDirectory(mProject), mProject.getWatermask()),
                        new File(outputFile.getParent(), "LST_Day_1km.tif")
                ).filter();
                FileUtils.deleteQuietly(new File(outputFile.getParent(), "filtered.LST_Day_1km.tif"));

                new GdalFilterWithWatermask(
                        new File(outputFile.getParent(), "filtered.LST_Night_1km.tif"),
                        new File(DirectoryLayout.getSettingsDirectory(mProject), mProject.getWatermask()),
                        new File(outputFile.getParent(), "LST_Night_1km.tif")
                ).filter();
                FileUtils.deleteQuietly(new File(outputFile.getParent(), "filtered.LST_Night_1km.tif"));
                break;
            default:
                throw new IllegalArgumentException();
            }

            // Write a metadata file
            final File metadataFile = getMetadataFile();
            FileUtils.forceMkdir(metadataFile.getParentFile());
            makeMetadata().toFile(metadataFile);
        } catch (Exception e) {
            FileUtils.deleteDirectory(getOutputFile().getParentFile());
            throw e;
        }
    }

    @Override
    public String getName() {
        return String.format(
                "Reproject and mosaic MODIS: project=\"%s\", product=%s, date=%s",
                mProject.getName(),
                mProduct,
                mDate.toCompactString()
        );
    }


}
