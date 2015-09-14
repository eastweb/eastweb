package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class GdalProjectEto implements ProjectEto {

    public static void main(String[] args) throws Exception {
        ProjectInfo[] projects = Config.getInstance().getProjects();
        ProjectInfo selectedProject = null;
        for (ProjectInfo project : projects) {
            if (project.getName().equals("ga_aea")) {
                selectedProject = project;
            }
        }

        ProjectEto projecter = new GdalProjectEto();
        projecter.project(new File(args[0]), selectedProject, new File(args[1]));
    }

    @Override
    public void project(File eto, ProjectInfo project, File out)
            throws Exception {

        assert(project.getShapeFiles().size() > 0);

        GdalUtils.register();

        synchronized (GdalUtils.lockObject) {
            // Load ETo and features
            Dataset inputDS = gdal.Open(eto.getPath());

            SpatialReference inputRef = new SpatialReference();
            inputRef.ImportFromWkt("GEOGCS[\"GCS_North_American_1983\",DATUM[\"D_North_American_1983\",SPHEROID[\"GRS_1980\",6378137,298.257222101]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]]");
            inputDS.SetProjection(inputRef.ExportToWkt());

            List<DataSource> features = new ArrayList<DataSource>();
            for (String filename : project.getShapeFiles()) {
                features.add(ogr.Open(new File(DirectoryLayout.getSettingsDirectory(project), filename).getPath()));
            }

            // Find union of extents
            double[] extent = features.get(0).GetLayer(0).GetExtent(); // Ordered: left, right, bottom, top

            System.out.println(Arrays.toString(extent));

            double left = extent[0];
            double right = extent[1];
            double bottom = extent[2];
            double top = extent[3];
            for (int i=1; i<features.size(); i++) {
                extent = features.get(i).GetLayer(0).GetExtent();
                if (extent[0] < left) {
                    left = extent[0];
                } else if (extent[1] > right) {
                    right = extent[1];
                } else if (extent[2] < bottom) {
                    bottom = extent[2];
                } else if (extent[3] > top) {
                    top = extent[3];
                }
            }

            // Project to union of extents
            Dataset outputDS = gdal.GetDriverByName("GTiff").Create(
                    out.getPath(),
                    (int) Math.ceil((right-left)/project.getProjection().getPixelSize()),
                    (int) Math.ceil((top-bottom)/project.getProjection().getPixelSize()),
                    1,
                    gdalconst.GDT_Float32
                    );

            // FIXME: hack --should get projection from project info somehow
            String outputProjection = features.get(0).GetLayer(0).GetSpatialRef().ExportToWkt();
            System.out.println(outputProjection);
            System.out.println(inputRef.ExportToWkt());

            outputDS.SetProjection(outputProjection);
            outputDS.SetGeoTransform(new double[] {
                    left, project.getProjection().getPixelSize(), 0,
                    top, 0, -project.getProjection().getPixelSize()
            });

            gdal.ReprojectImage(inputDS, outputDS, null, null, gdalconst.GRA_Cubic);

            outputDS.GetRasterBand(1).ComputeStatistics(false);

            outputDS.delete();
            inputDS.delete();
        }

    }

}
