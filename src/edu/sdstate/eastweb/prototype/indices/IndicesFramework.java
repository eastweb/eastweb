package edu.sdstate.eastweb.prototype.indices;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;

// FIXME: problem, using only the enviromentalIndex as a key will not work
// because it is a static enum. need to make a dynamic enum instead

public abstract class IndicesFramework {
    public IndexCalculator LST_DAY;
    public IndexCalculator LST_NIGHT;
    public IndexCalculator LST_MEAN;
    public IndexCalculator NDVI;
    public IndexCalculator EVI;
    public IndexCalculator NDWI5;
    public IndexCalculator NDWI6;
    public IndexCalculator SAVI;
    public IndexCalculator ETA;
    public IndexCalculator TRMM;
    public IndexCalculator TRMM_RT;

    private File red;
    private File nir;
    private File day;
    private File night;
    private File output;
    private File blue;
    private File swir;
    private File swir2;
    private File elevation;
    private File eto;
    private File trmm;
    private File trmmrt;

    public Map<EnvironmentalIndex, IndexCalculator> IndicesMap;
    public Map<String, IndexCalculator> StringIndicesMap;

    public IndicesFramework(ProjectInfo mProject, EnvironmentalIndex index,
            DataDate date, String feature) {

        SetFiles(mProject, index, date, feature);
        // SetUpBaseIndicies(mProject);

        try {
            SetNewIndices();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private void SetUpBaseIndicies(ProjectInfo mProject) {

        IndexCalculator LST_DAY =
                new GdalFilterCalculator(day, output,
                        mProject.getMinLst() + 273.15,
                        mProject.getMaxLst() + 273.15);
        IndexCalculator LST_NIGHT =
                new GdalFilterCalculator(night, output,
                        mProject.getMinLst() + 273.15,
                        mProject.getMaxLst() + 273.15);
        IndexCalculator LST_MEAN =
                new GdalMeanLstCalculator(day, night, output,
                        mProject.getMinLst() + 273.15,
                        mProject.getMaxLst() + 273.15);
        IndexCalculator NDVI = new GdalNdviCalculator(red, nir, output);
        IndexCalculator EVI = new GdalEviCalculator(red, nir, blue, output);
        IndexCalculator NDWI5 = new GdalNdwiCalculator(nir, swir, output);
        IndexCalculator NDWI6 = new GdalNdwiCalculator(nir, swir2, output);
        IndexCalculator SAVI = new GdalSaviCalculator(red, nir, output);
        IndexCalculator ETA =
                new GdalEtaCalculator(day, elevation, eto, output,
                        mProject.getMinLst() + 273.15,
                        mProject.getMaxLst() + 273.15);
        IndexCalculator TRMM = new GdalDummyCalculator(trmm, output);
        IndexCalculator TRMM_RT = new GdalDummyCalculator(trmmrt, output);
    }

    private void SetFiles(ProjectInfo mProject, EnvironmentalIndex mIndex,
            DataDate mDate, String mFeature) {

        try {
            FileUtils.forceMkdir(output.getParentFile());

            String feature = new File(mFeature).getName().split("\\.")[0];

            red =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature,
                            "Nadir_Reflectance_Band1");
            nir =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature,
                            "Nadir_Reflectance_Band2");
            day =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.LST, feature, "LST_Day_1km");
            night =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.LST, feature, "LST_Night_1km");
            output =
                    DirectoryLayout.getIndex(mProject, mIndex, mDate, mFeature);
            blue =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature,
                            "Nadir_Reflectance_Band3");
            swir =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature,
                            "Nadir_Reflectance_Band5");
            swir2 =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature,
                            "Nadir_Reflectance_Band6");
            elevation =
                    new File(DirectoryLayout.getSettingsDirectory(mProject),
                            mProject.getElevation());
            eto = DirectoryLayout.getEtoReprojected(mProject, mDate);
            trmm =
                    DirectoryLayout.getTrmmClip(mProject,
                            TrmmProduct.TRMM_3B42, mDate, feature);
            trmmrt =
                    DirectoryLayout.getTrmmClip(mProject,
                            TrmmProduct.TRMM_3B42RT, mDate, feature);
        } catch (Exception e) {
        }
    }

    public abstract void SetNewIndices() throws Exception;
}
