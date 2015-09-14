package edu.sdstate.eastweb.prototype.reprojection;

import java.io.File;
import java.util.ArrayList;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class ModisReprojection {
    private ArrayList<File> mozaicOutput;

    public ModisReprojection() throws Exception {

    }

    public void project(File[] input, ProjectInfo project, File[] outputFiles,
            int[] band) throws Exception {
        Mozaic myMozaic=new Mozaic(input, band);
        mozaicOutput=myMozaic.run();
        reproject(project,band,outputFiles);
    }

    private void reproject(ProjectInfo project, int[] band, File[] outputFiles) throws ConfigReadException {
        for(int index=0; index<band.length; index++){
            File nonProjectFile=mozaicOutput.get(index);
            File output=new File(outputFiles[index].getPath());
            //TODO: GdalUtils.project() doesn't use wtk right now, may change later
            String wtk="  ";
            GdalUtils.project(wtk,nonProjectFile, project,output);

        }
    }

}
