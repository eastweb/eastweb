package version2.prototype;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class InitializeMockData {

    public ProjectInfo projectInfo;
    public Config config;
    public PluginMetaDataCollection pluginMetaDataCollection;
    public File ShapeFile;
    public File OutTableFile;
    public List<String> ListOfDisplaySummary;
    public ArrayList<String> SummarySingletonNames;

    public InitializeMockData() throws ConfigReadException, Exception
    {
        projectInfo = Config.getInstance().loadProject("tw_test"); // load project should be abstract in a different place
        config = Config.getInstance();
        pluginMetaDataCollection = new PluginMetaDataCollection(projectInfo.getPlugin());
        ShapeFile = new File("");
        OutTableFile = new File("");
        ListOfDisplaySummary = new ArrayList<String> ();
        SummarySingletonNames = new ArrayList<String>(Arrays.asList("Count", "Sum", "Mean", "StdDev"));
    }
}
