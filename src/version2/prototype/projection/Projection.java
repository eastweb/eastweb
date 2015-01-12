package version2.prototype.projection;

import java.io.File;

import version2.prototype.ConfigReadException;
import version2.prototype.ProjectInfo;

public interface Projection {
    void project(File inFile, ProjectInfo projectInfo, File outFile) throws ConfigReadException;

    void run();
}
