package version2.prototype;



public class InitializeMockData {

    public ProjectInfo projectInfo;
    public Config config;
    public PluginMetaDataCollection pluginMetaDataCollection;

    public InitializeMockData() throws ConfigReadException, Exception
    {
        projectInfo = Config.getInstance().loadProject("tw_test"); // load project should be abstract in a different place
        config = Config.getInstance();
        pluginMetaDataCollection = new PluginMetaDataCollection(projectInfo.getPlugin());
    }
}
