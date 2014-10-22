package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.File;
import java.lang.reflect.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.MetaData;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.indices.IndicesFramework;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;
import edu.sdstate.eastweb.prototype.indices.IndexCalculator;
import edu.sdstate.eastweb.prototype.indices.IndexMetadata;
import edu.sdstate.eastweb.prototype.reprojection.EtoReprojectedMetadata;
import edu.sdstate.eastweb.prototype.reprojection.ModisReprojectedMetadata;
import edu.sdstate.eastweb.prototype.reprojection.TrmmReprojectedMetadata;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

@SuppressWarnings("serial")
public class GdalCalculateIndexTask implements RunnableTask {
    private final ProjectInfo mProject;
    private final EnvironmentalIndex mIndex;
    private final DataDate mDate;
    private final String mFeature;


    public GdalCalculateIndexTask(ProjectInfo project,
            EnvironmentalIndex index, DataDate date, String feature) {
        mProject = project;
        mIndex = index;
        mDate = date;
        mFeature = feature;
    }

    @Override
    public String getName() {
        return String.format(
                "Calculate index: project=%s index=%s date=%s feature=%s",
                mProject.getName(), mIndex.toString(), mDate.toCompactString(),
                mFeature);
    }

    private File getMetadataFile(String shapeFile) throws ConfigReadException {
        return DirectoryLayout.getIndexMetadata(mProject, mIndex, mDate,
                shapeFile);
    }

    private IndexMetadata makeMetadata(String shapeFile) throws IOException {
        final List<ModisReprojectedMetadata> modis =
            new ArrayList<ModisReprojectedMetadata>();

        for (ModisProduct product : getModisProducts()) {
            modis.add(ModisReprojectedMetadata.fromFile(DirectoryLayout
                    .getModisReprojectedMetadata(mProject, product, mDate)));
        }

        final List<TrmmReprojectedMetadata> trmm =
            new ArrayList<TrmmReprojectedMetadata>();
        final List<EtoReprojectedMetadata> eto =
            new ArrayList<EtoReprojectedMetadata>();
        final long timestamp = new Date().getTime();

        return new IndexMetadata(modis, trmm, eto, shapeFile, timestamp);
    }

    private List<ModisProduct> getModisProducts() {
        switch (mIndex) {
        case LST_DAY:
        case LST_NIGHT:
        case LST_MEAN:
        case ETA:
            return Arrays.asList(ModisProduct.LST);

        case NDVI:
        case EVI:
        case NDWI5:
        case NDWI6:
        case SAVI:
            return Arrays.asList(ModisProduct.NBAR);

        case TRMM:
        case TRMM_RT:
        case NLDAS:
            return Collections.emptyList();

        default:
            throw new IllegalArgumentException();
        }
    }

    private IndexCalculator makeCalculator() throws IOException, SQLException,
    NoSuchFieldException, SecurityException, IllegalArgumentException,
    IllegalAccessException, ClassNotFoundException, ParserConfigurationException, SAXException, NoSuchMethodException, InstantiationException, InvocationTargetException {

        //replace mIndex with MetaData.GetInstance().IndicesMetaData when ready with schedule
        Class<?> clazz = Class.forName("edu.sdstate.eastweb.prototype.indices." + "Gdal" + mIndex.name()  + "Calculator");
        Constructor<?> ctor = clazz.getConstructor(ProjectInfo.class, DataDate.class, String.class, EnvironmentalIndex.class);
        Object object = ctor.newInstance(new Object[] { mProject, mDate, new File(mFeature).getName().split("\\.")[0], mIndex });
        return (IndexCalculator) object;
    }

    @Override
    public void run() throws Exception {
        IndexCalculator calculator = makeCalculator();

        calculator.calculate();

        final File metadataFile = getMetadataFile(mFeature);
        FileUtils.forceMkdir(metadataFile.getParentFile());
        makeMetadata(mFeature).toFile(metadataFile);
    }

    @Override
    public boolean getCanSkip() {
        try {
            return IndexMetadata.fromFile(getMetadataFile(mFeature))
            .equalsIgnoreTimestamp(makeMetadata(mFeature));
        } catch (Exception e) {
            return false;
        }
    }

}
