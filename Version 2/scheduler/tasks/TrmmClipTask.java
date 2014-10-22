package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.File;

import org.apache.commons.io.FileUtils;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.indices.Clipper;
import edu.sdstate.eastweb.prototype.indices.GdalClipper;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

public class TrmmClipTask implements RunnableTask {

    private static final long serialVersionUID = 1L;
    private ProjectInfo project;
    private TrmmProduct product;
    private DataDate date;
    private String feature;

    public TrmmClipTask(ProjectInfo project, TrmmProduct product,
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

    @Override
    public void run() throws Exception {
        final File input = DirectoryLayout.getTrmmReprojected(project, product, date);
        final File output = DirectoryLayout.getTrmmClip(project, product, date, new File(feature).getName().split("\\.")[0]); // FIXME: ugly!!!

        FileUtils.forceMkdir(output.getParentFile());

        Clipper clipper = new GdalClipper(
                input,
                new File(DirectoryLayout.getSettingsDirectory(project), feature), // FIXME
                output,
                "GTiff"
        );

        clipper.clip();
    }

    @Override
    public boolean getCanSkip() {
        try {
            // FIXME: use metadata instead
            final File output = DirectoryLayout.getTrmmClip(project, product, date, new File(feature).getName().split("\\.")[0]); // FIXME: ugly!!!
            if (!output.exists()) {
                return false;
            }
            return true;
        } catch (ConfigReadException e) {
            return false;
        }
    }

}
