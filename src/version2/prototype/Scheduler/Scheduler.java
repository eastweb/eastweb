package version2.prototype.Scheduler;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import version2.prototype.Config;
import version2.prototype.DataDate;
import version2.prototype.InitializeMockData;
import version2.prototype.PluginMetaDataCollection;
import version2.prototype.PluginMetaDataCollection.DownloadMetaData;
import version2.prototype.PluginMetaDataCollection.ProcessMetaData;
import version2.prototype.ProjectInfo;
import version2.prototype.ZonalSummary;
import version2.prototype.indices.IndexCalculator;
import version2.prototype.summary.SummaryData;
import version2.prototype.summary.TemporalSummaryCalculator;
import version2.prototype.summary.ZonalSummaryCalculator;

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

    public void run() throws Exception
    {
        for(String item: projectInfo.getPlugin())
        {
            //NldasDownloadTask download = new NldasDownloadTask(projectInfo.getStartDate(), PluginMetaDataCollection.instance.get(item).Download);
            //download.run();

            RunDownloader(item);
            RunProcess(item);
            RunIndicies(item);
            RunSummary(item);
        }
    }

    public void RunDownloader(String pluginName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // uses reflection
        Class<?> clazzDownloader = Class.forName("version2.prototype.projection." + "NldasDownloadTask"); // TODO: need to change so that it calls base on the metaData
        Constructor<?> ctorDownloader = clazzDownloader.getConstructor(ProjectInfo.class, DownloadMetaData.class);
        Object downloader =  ctorDownloader.newInstance(new Object[] {projectInfo.getStartDate(), PluginMetaDataCollection.instance.get(pluginName).Download});
        Method methodDownloader = downloader.getClass().getMethod("Run");
        methodDownloader.invoke(downloader);
    }

    public void RunProcess(String pluginName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        ProcessMetaData temp = PluginMetaDataCollection.instance.get(pluginName).Projection;

        for (int i = 0; i < temp.processStep.size(); i++)
        {
            Class<?> clazzProcess = Class.forName("version2.prototype.projection." + temp.processStep.get(i));
            Constructor<?> ctorProcess = clazzProcess.getConstructor(ProcessReflectionData.class);
            Object process =  ctorProcess.newInstance(new Object[] {new ProcessReflectionData()});
            Method methodProcess = process.getClass().getMethod("Run");
            methodProcess.invoke(process);
        }
    }

    public void RunIndicies(String pluginName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // get data for data, file, and the last thing that i dont really know about
        // ask jiameng what the hell the file is suppose to do
        for(String indexCalculatorItem: PluginMetaDataCollection.instance.get(pluginName).IndicesMetaData)
        {
            Class<?> clazzIndicies = Class.forName("version2.prototype.indices." + "Gdal" + indexCalculatorItem + "Calculator");
            Constructor<?> ctorIndicies = clazzIndicies.getConstructor(ProjectInfo.class, DataDate.class, String.class, String.class);
            Object indexCalculator =  ctorIndicies.newInstance(
                    new Object[] {
                            projectInfo,
                            projectInfo.getStartDate(),
                            new File(indexCalculatorItem).getName().split("\\.")[0],
                            indexCalculatorItem}
                    );
            Method methodIndicies = indexCalculator.getClass().getMethod("Run");
            methodIndicies.invoke(indexCalculator);
        }
    }

    public void RunSummary(String pluginName) throws Exception
    {
        if(PluginMetaDataCollection.instance.get(pluginName).Summary.IsTeamporalSummary)
        {
            TemporalSummaryCalculator temporalSummaryCal = new TemporalSummaryCalculator(new SummaryData());
            temporalSummaryCal.run();
        }

        for(ZonalSummary summary: projectInfo.getZonalSummaries())
        {
            ZonalSummaryCalculator zonalSummaryCal = new ZonalSummaryCalculator(new SummaryData());
            zonalSummaryCal.run();
        }
    }
}

