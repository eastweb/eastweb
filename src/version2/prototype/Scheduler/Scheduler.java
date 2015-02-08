package version2.prototype.Scheduler;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import version2.prototype.Config;
import version2.prototype.ConfigReadException;
import version2.prototype.DataDate;
import version2.prototype.DirectoryLayout;
import version2.prototype.ProjectInfo;
import version2.prototype.ZonalSummary;
import version2.prototype.PluginMetaData.PluginMetaDataCollection;
import version2.prototype.PluginMetaData.PluginMetaDataCollection.DownloadMetaData;
import version2.prototype.PluginMetaData.PluginMetaDataCollection.ProcessMetaData;
import version2.prototype.projection.PrepareProcessTask;
import version2.prototype.projection.ProcessData;
import version2.prototype.summary.SummaryData;
import version2.prototype.summary.ZonalSummaryCalculator;

public class Scheduler {

    private static Scheduler instance;
    public ProjectInfo projectInfo;
    public Config config;
    public PluginMetaDataCollection pluginMetaDataCollection;
    private File outTable;
    private ArrayList<String> summarySingletonNames;

    private Scheduler(SchedulerData data)
    {
        projectInfo = data.projectInfo;
        config = data.config;
        pluginMetaDataCollection = data.pluginMetaDataCollection;
        outTable = data.OutTableFile;
        summarySingletonNames = data.SummarySingletonNames;
    }

    public static Scheduler getInstance(SchedulerData data)
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
            RunDownloader(item);
            RunProcess(item);
            RunIndicies(item);
            RunSummary(item);
        }
    }

    public void RunDownloader(String pluginName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // uses reflection
        Class<?> clazzDownloader = Class.forName("version2.prototype.download."
                + PluginMetaDataCollection.instance.get(pluginName).Title
                + PluginMetaDataCollection.instance.get(pluginName).Download.className);
        Constructor<?> ctorDownloader = clazzDownloader.getConstructor(DataDate.class, DownloadMetaData.class);
        Object downloader =  ctorDownloader.newInstance(new Object[] {projectInfo.getStartDate(), PluginMetaDataCollection.instance.get(pluginName).Download});
        Method methodDownloader = downloader.getClass().getMethod("run");
        methodDownloader.invoke(downloader);
    }

    public void RunProcess(String pluginName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ConfigReadException
    {
        ProcessMetaData temp = PluginMetaDataCollection.instance.get(pluginName).Projection;
        PrepareProcessTask prepareProcessTask = new PrepareProcessTask(projectInfo, "NBAR", projectInfo.getStartDate());

        for (int i = 1; i <= temp.processStep.size(); i++)
        {
            if(temp.processStep.get(i) != null && !temp.processStep.get(i).isEmpty())
            {
                Class<?> clazzProcess = Class.forName("version2.prototype.projection."
                        + PluginMetaDataCollection.instance.get(pluginName).Title
                        + temp.processStep.get(i));
                Constructor<?> ctorProcess = clazzProcess.getConstructor(ProcessData.class);
                Object process =  ctorProcess.newInstance(new Object[] {new ProcessData(
                        prepareProcessTask.getInputFiles(),
                        prepareProcessTask.getBands(),
                        prepareProcessTask.getInputFile(),
                        prepareProcessTask.getOutputFile(),
                        projectInfo)});
                Method methodProcess = process.getClass().getMethod("run");
                //methodProcess.invoke(process);
            }
        }
    }

    public void RunIndicies(String pluginName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // get data for data, file, and the last thing that i dont really know about
        // ask jiameng what the hell the file is suppose to do
        for(String indexCalculatorItem: PluginMetaDataCollection.instance.get(pluginName).IndicesMetaData)
        {
            Class<?> clazzIndicies = Class.forName("version2.prototype.indices."
                    + PluginMetaDataCollection.instance.get(pluginName).Title
                    + indexCalculatorItem);
            Constructor<?> ctorIndicies = clazzIndicies.getConstructor(String.class, DataDate.class, String.class, String.class);
            Object indexCalculator =  ctorIndicies.newInstance(
                    new Object[] {
                            projectInfo.getName(),
                            projectInfo.getStartDate(),
                            new File(indexCalculatorItem).getName().split("\\.")[0],
                            indexCalculatorItem}
                    );
            Method methodIndicies = indexCalculator.getClass().getMethod("calculate");
            //methodIndicies.invoke(indexCalculator);
        }
    }

    public void RunSummary(String pluginName) throws Exception
    {
        if(PluginMetaDataCollection.instance.get(pluginName).Summary.IsTeamporalSummary)
        {
            /**
             * <p>Use this when you want to send data to a TemporalSummaryCalculator object.</p>
             * 
             * @param inRaster - Type: File[] - A File object for each DataDate.<br/>
             * Example:  <code>{@link #version2.prototype.DirectoryLayout.getIndexMetadata(ProjectInfo, String, DataDate, String)}<br/>
             * getIndexMetadata(mProject, mIndex, sDate, zone.getShapeFile())</code>
             * @param inShape - Type: File - The layer/shape file.<br/>
             * Example:  <code>File({@link #version2.prototype.DirectoryLayout.getSettingsDirectory(ProjectInfo)},
             * {@link #version2.prototype.ZonalSummary.getShapeFile()})<br/>
             * File(DirectoryLayout.getSettingsDirectory(mProject), zone.getShapeFile())</code>
             * @param outTable - Type: File - File object pointing to output location for zonal summary
             * Example:  <code>for ({@link version2.prototype.ZonalSummary ZonalSummary} zone : mProject.{@link #version2.prototype.ProjectInfo.getSummaries()}) { zone.{@link #version2.prototype.ZonalSummary.getField()}; }<br/>
             * for (ZonalSummary zone : mProject.getSummaries())<br/>  { zone.getField(); }</code>
             * @param inDate - Type: DataDate[] - An array of the dates of the downloaded data to be used in finding the data in the file system and in processing temporal summaries.<br/>
             * @param hrsPerInputData - Type: int - The number of hours each piece of downloaded data represents.
             * @param hrsPerOutputData - Type: int - The number of hours each piece of summary/output data will represent.
             * @param projectSDate - Type: Calendar - The projects start date.
             * @param calStrategy - Type: CalendarStrategy - The strategy to use when getting the starting date of the week.
             * @param merStrategy - Type: MergeStrategy - The strategy to use when merging downloaded data.
             * @param tempMethods - Type: ArrayList<TemporalSummary> - The list of summary methods to calculate for temporal summary.
             * @param mergMethods - Type: ArrayList<MergeSummary> - The list of summary methods to use during merging with the chosen MergeStrategy.


            TemporalSummaryCalculator temporalSummaryCal = new TemporalSummaryCalculator(new SummaryData(
                    File[] inRaster,
                    new File(DirectoryLayout.getSettingsDirectory(projectInfo), zone.getShapeFile()),
                    outTable,
                    DataDate[] inDate,
                    int hrsPerInputData,
                    int hrsPerOutputData,
                    Calendar projectSDate,
                    CalendarStrategy calStrategy,
                    MergeStrategy merStrategy,
                    ArrayList<TemporalSummary> tempMethods,
                    ArrayList<MergeSummary> mergMethods));
            temporalSummaryCal.run();*/
        }

        for(ZonalSummary zone: projectInfo.getZonalSummaries())
        {
            ZonalSummaryCalculator zonalSummaryCal = new ZonalSummaryCalculator(new SummaryData(
                    DirectoryLayout.getIndexMetadata(projectInfo, "nldas", projectInfo.getStartDate(), zone.getShapeFile()),
                    new File(DirectoryLayout.getSettingsDirectory(projectInfo), zone.getShapeFile()),
                    outTable,
                    zone.getField(),
                    summarySingletonNames, null, 0, 0, null, null, null, null, null));
            zonalSummaryCal.run();
        }
    }
}

