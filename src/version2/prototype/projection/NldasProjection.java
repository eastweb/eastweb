package version2.prototype.projection;

import java.io.File;

import version2.prototype.ConfigReadException;
import version2.prototype.ProjectInfo;
import version2.prototype.util.GdalUtils;

public class NldasProjection implements Projection{
    @Override
    public void project(ProcessData data) throws Exception {
        project(data.input, data.projectInfo, data.output);
    }

    public void project(File inFile, ProjectInfo projectInfo, File outFile) throws ConfigReadException {
        GdalUtils.project(inFile, projectInfo, outFile);

    }
}