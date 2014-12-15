package version2.prototype.projection;

import java.io.File;

import version2.prototype.ProjectInfo;
import version2.prototype.util.GdalUtils;

public class NldasProjection implements Projection{
    @Override
    public void project(File input, ProjectInfo project, File out) throws Exception {
        GdalUtils.project(input, project,out);
    }
}