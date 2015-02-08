package version2.prototype.Scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import version2.prototype.Config;
import version2.prototype.ConfigReadException;
import version2.prototype.ProjectInfo;
import version2.prototype.PluginMetaData.PluginMetaDataCollection;

public class SchedulerData {

    public ProjectInfo projectInfo;
    public Config config;
    public PluginMetaDataCollection pluginMetaDataCollection;
    public File ShapeFile;
    public File OutTableFile;
    public List<String> ListOfDisplaySummary;
    public ArrayList<String> SummarySingletonNames;

    public SchedulerData() throws ConfigReadException, Exception
    {
        config = Config.getInstance();
        projectInfo = config.loadProject("tw_test"); // load project should be abstract in a different place

        ShapeFile = new File("");
        OutTableFile = new File("");
        ListOfDisplaySummary = new ArrayList<String> ();
        pluginMetaDataCollection = new PluginMetaDataCollection(projectInfo.getPlugin());
        SummarySingletonNames = new ArrayList<String>(Arrays.asList("Count", "Sum", "Mean", "StdDev"));
    }
}
