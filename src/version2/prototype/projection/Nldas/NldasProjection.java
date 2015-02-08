package version2.prototype.projection.Nldas;

import java.io.File;

import version2.prototype.ConfigReadException;
import version2.prototype.ProjectInfo;
import version2.prototype.projection.ProcessData;
import version2.prototype.projection.ProjectionFramework;
import version2.prototype.util.GdalUtils;

public class NldasProjection implements ProjectionFramework{
    public NldasProjection(ProcessData data) throws ConfigReadException {
        project(data.input, data.projectInfo, data.output);
    }

    @Override
    public void project(File inFile, ProjectInfo projectInfo, File outFile) throws ConfigReadException {
        GdalUtils.project(inFile, projectInfo, outFile);
    }

    @Override
    public void run(){};
}