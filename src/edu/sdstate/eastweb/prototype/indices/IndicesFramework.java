package edu.sdstate.eastweb.prototype.indices;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;


// FIXME: problem, using only the enviromentalIndex as a key will not work because it is a static enum. need to make a dynamic enum instead

public abstract class IndicesFramework {
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

    public IndicesFramework(ProjectInfo mProject, EnvironmentalIndex index,
            DataDate date, String feature) {

        SetFiles(mProject, index, date, feature);

        IndicesMap = new HashMap<EnvironmentalIndex, IndexCalculator>();
        IndicesMap.put(EnvironmentalIndex.LST_DAY,
                new GdalFilterCalculator(day, output,
                        mProject.getMinLst() + 273.15,
                        mProject.getMaxLst() + 273.15));
        IndicesMap.put(EnvironmentalIndex.LST_NIGHT,
                new GdalFilterCalculator(night, output,
                        mProject.getMinLst() + 273.15,
                        mProject.getMaxLst() + 273.15));
        IndicesMap.put(
                EnvironmentalIndex.LST_MEAN,
                new GdalMeanLstCalculator(day, night, output, mProject
                        .getMinLst() + 273.15, mProject.getMaxLst() + 273.15));
        IndicesMap.put(EnvironmentalIndex.NDVI, new GdalNdviCalculator(red,
                nir, output));
        IndicesMap.put(EnvironmentalIndex.EVI, new GdalEviCalculator(red, nir,
                blue, output));
        IndicesMap.put(EnvironmentalIndex.NDWI5, new GdalNdwiCalculator(nir,
                swir, output));
        IndicesMap.put(EnvironmentalIndex.NDWI6, new GdalNdwiCalculator(nir,
                swir2, output));
        IndicesMap.put(EnvironmentalIndex.SAVI, new GdalSaviCalculator(red,
                nir, output));
        IndicesMap.put(
                EnvironmentalIndex.ETA,
                new GdalEtaCalculator(day, elevation, eto, output, mProject
                        .getMinLst() + 273.15, mProject.getMaxLst() + 273.15));
        IndicesMap.put(EnvironmentalIndex.TRMM, new GdalDummyCalculator(trmm,
                output));
        IndicesMap.put(EnvironmentalIndex.TRMM_RT, new GdalDummyCalculator(
                trmmrt, output));

        try {
            AddMaps();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void SetFiles(ProjectInfo mProject, EnvironmentalIndex mIndex,
            DataDate mDate, String mFeature) {

        try{
            FileUtils.forceMkdir(output.getParentFile());

            String feature = new File(mFeature).getName().split("\\.")[0];

            red =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature, "Nadir_Reflectance_Band1");
            nir =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature, "Nadir_Reflectance_Band2");
            day =
                    DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.LST,
                            feature, "LST_Day_1km");
            night =
                    DirectoryLayout.getModisClip(mProject, mDate, ModisProduct.LST,
                            feature, "LST_Night_1km");
            output =
                    DirectoryLayout.getIndex(mProject, mIndex, mDate, mFeature);
            blue =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature, "Nadir_Reflectance_Band3");
            swir =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature, "Nadir_Reflectance_Band5");
            swir2 =
                    DirectoryLayout.getModisClip(mProject, mDate,
                            ModisProduct.NBAR, feature, "Nadir_Reflectance_Band6");
            elevation =
                    new File(DirectoryLayout.getSettingsDirectory(mProject),
                            mProject.getElevation());
            eto = DirectoryLayout.getEtoReprojected(mProject, mDate);
            trmm =
                    DirectoryLayout.getTrmmClip(mProject, TrmmProduct.TRMM_3B42,
                            mDate, feature);
            trmmrt =
                    DirectoryLayout.getTrmmClip(mProject, TrmmProduct.TRMM_3B42RT,
                            mDate, feature);
        }
        catch(Exception e){}
    }

    public abstract void AddMaps() throws Exception;
}
