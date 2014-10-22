package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;

import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class NldasProjection implements Projection{
    @Override
    public void project(File input, ProjectInfo project, File out)
    throws Exception {
        GdalUtils.project(input, project,out);
    }

}
