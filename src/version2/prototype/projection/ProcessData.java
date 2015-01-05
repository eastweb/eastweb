package version2.prototype.projection;

import java.io.File;

import version2.prototype.ProjectInfo;

public class ProcessData {

    public File[] inputArray;
    public int[] bandArray;
    public File input;
    public File output;
    public ProjectInfo projectInfo;

    public ProcessData()
    {

    }

    public ProcessData(File[] inputArray, int[] bandArray, File input, File output, ProjectInfo project)
    {
        this.inputArray = inputArray;
        this.bandArray = bandArray;
        this.input = input;
        this.output = output;
        projectInfo = project;
    }
}
