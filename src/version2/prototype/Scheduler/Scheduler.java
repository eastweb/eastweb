package version2.prototype.Scheduler;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;
import edu.sdstate.eastweb.prototype.indices.IndexCalculator;
import version2.prototype.Config;
import version2.prototype.InitializeMockData;
import version2.prototype.PluginMetaDataCollection;
import version2.prototype.PluginMetaDataCollection.DownloadMetaData;
import version2.prototype.ProjectInfo;

public class Scheduler {

    private static Scheduler instance;
    public ProjectInfo projectInfo;
    public Config config;
    public PluginMetaDataCollection pluginMetaDataCollection;

    private Scheduler(InitializeMockData data)
    {
        projectInfo = data.projectInfo;
        config = data.config;
        pluginMetaDataCollection = data.pluginMetaDataCollection;
    }

    public static Scheduler getInstance(InitializeMockData data)
    {
        if(instance == null) {
            instance = new Scheduler(data);
        }

        return instance;
    }

    // TODO: change to use reflection to call classes
    public void run() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        for(String item: projectInfo.getPlugin())
        {
            DownloadMetaData downloadMetaData = PluginMetaDataCollection.instance.get(item).Download;
            NldasDownloadTask download = new NldasDownloadTask(projectInfo.getStartDate(), downloadMetaData);
            download.run();

            // Process
            // base on processStep

            // get data for data, file, and the last thing that i dont really know about
            // ask jiameng what the hell the file is suppose to do
            for(String indexCalculatorItem: PluginMetaDataCollection.instance.get(item).IndicesMetaData)
            {
                Class<?> clazz = Class.forName("edu.sdstate.eastweb.prototype.indices." + "Gdal" + indexCalculatorItem + "Calculator");
                Constructor<?> ctor = clazz.getConstructor(ProjectInfo.class, DataDate.class, String.class, EnvironmentalIndex.class);
                IndexCalculator indexCalculator = (IndexCalculator) ctor.newInstance(new Object[] {
                        projectInfo,
                        projectInfo.getStartDate(),
                        new File(indexCalculatorItem).getName().split("\\.")[0],
                        indexCalculatorItem });
            }


        }
    }
}
