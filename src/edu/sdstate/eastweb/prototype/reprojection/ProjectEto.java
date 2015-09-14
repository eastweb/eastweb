package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;

import edu.sdstate.eastweb.prototype.ProjectInfo;

public interface ProjectEto {

    void project(File eto, ProjectInfo project, File out) throws Exception;

}
