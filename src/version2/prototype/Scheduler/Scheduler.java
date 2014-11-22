package version2.prototype.Scheduler;

import version2.prototype.Config;
import version2.prototype.InitializeMockData;
import version2.prototype.PluginMetaDataCollection;
import version2.prototype.PluginMetaDataCollection.DownloadMetaData;
import version2.prototype.ProjectInfo;

public class Scheduler {

    private static Scheduler instance;
    private ProjectInfo projectInfo;
    private Config config;
    private PluginMetaDataCollection pluginMetaDataCollection;

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
    public void run()
    {
        for(String item: projectInfo.getPlugin())
        {
            DownloadMetaData downloadMetaData = PluginMetaDataCollection.instance.get(item).Download;
            NldasDownloadTask download = new NldasDownloadTask(projectInfo.getStartDate(), downloadMetaData);
            download.run();


        }
    }
}
