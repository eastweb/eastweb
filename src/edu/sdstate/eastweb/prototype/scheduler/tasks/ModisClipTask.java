package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.indices.Clipper;
import edu.sdstate.eastweb.prototype.indices.GdalClipper;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

public class ModisClipTask implements RunnableTask {

    private ProjectInfo project;
    private ModisProduct product;
    private DataDate date;
    private String feature;

    public ModisClipTask(ProjectInfo project, ModisProduct product,
            DataDate date, String feature) {
        this.project = project;
        this.product = product;
        this.date = date;
        this.feature = feature;
    }

    @Override
    public String getName() {
        return String.format(
                "Clip Raster: project=%s product=%s date=%s feature=%s",
                project.getName(),
                product,
                date.toCompactString(),
                feature
                );
    }

    // FIXME: ugly
    private File[] getInputs() throws ConfigReadException {
        File[] inputs;
        switch (product) {
        case LST:
            inputs = new File[] {
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "LST_Day_1km"),
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "LST_Night_1km")
            };
            break;
        case NBAR:
            inputs = new File[] {
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "Nadir_Reflectance_Band1"),
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "Nadir_Reflectance_Band2"),
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "Nadir_Reflectance_Band3"),
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "Nadir_Reflectance_Band4"),
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "Nadir_Reflectance_Band5"),
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "Nadir_Reflectance_Band6"),
                    DirectoryLayout.getModisReprojectedBand(project, product, date, "Nadir_Reflectance_Band7"),
            };
            break;
        default:
            throw new IllegalArgumentException("Unsupported MODIS product for MODIS clipping task.");
        }
        return inputs;
    }

    // FIXME: ugly
    private File[] getOutputs() throws ConfigReadException {
        File[] outputs;
        switch (product) {
        case LST:
            outputs = new File[] {
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "LST_Day_1km"),
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "LST_Night_1km")
            };
            break;
        case NBAR:
            outputs = new File[] {
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "Nadir_Reflectance_Band1"),
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "Nadir_Reflectance_Band2"),
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "Nadir_Reflectance_Band3"),
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "Nadir_Reflectance_Band4"),
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "Nadir_Reflectance_Band5"),
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "Nadir_Reflectance_Band6"),
                    DirectoryLayout.getModisClip(project, date, product, new File(feature).getName().split("\\.")[0], "Nadir_Reflectance_Band7")
            };
            break;
        default:
            throw new IllegalArgumentException("Unsupported MODIS product for MODIS clipping task.");
        }
        return outputs;
    }

    @Override
    public void run() throws Exception {
        File[] inputs = getInputs();
        File[] outputs = getOutputs();

        System.out.println(Arrays.toString(inputs));
        System.out.println(Arrays.toString(outputs));
        System.out.println(new File(DirectoryLayout.getSettingsDirectory(project), feature).getPath());

        for (int i=0; i<outputs.length; i++) {
            FileUtils.forceMkdir(outputs[i].getParentFile());

            Clipper clipper = new GdalClipper(
                    inputs[i],
                    new File(DirectoryLayout.getSettingsDirectory(project), feature), // FIXME
                    outputs[i],
                    "GTiff");

            clipper.clip();
        }
    }

    @Override
    public boolean getCanSkip() {
        try {
            // FIXME: use metadata instead
            for (File output : getOutputs()) {
                if (!output.exists()) {
                    return false;
                }
            }
            return true;
        } catch (ConfigReadException e) {
            return false;
        }
    }

}
