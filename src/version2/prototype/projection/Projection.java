package version2.prototype.projection;

import java.io.File;

import version2.prototype.ProjectInfo;

public interface Projection {
    void project(File input, ProjectInfo projectInfo, File output) throws Exception;
}
