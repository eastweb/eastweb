package version2.prototype.projection;

import java.io.File;

import edu.sdstate.eastweb.prototype.ProjectInfo;

public interface Projection {

    void project(File input, ProjectInfo projectInfo, File output) throws Exception;

}
