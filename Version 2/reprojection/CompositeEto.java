package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;
import java.util.List;

public interface CompositeEto {

    void composite(List<File> inputs, File output) throws Exception;

}
