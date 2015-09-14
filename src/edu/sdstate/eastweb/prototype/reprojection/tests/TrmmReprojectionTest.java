package edu.sdstate.eastweb.prototype.reprojection.tests;

import java.io.File;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.Projection;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.reprojection.TrmmProjection;
import edu.sdstate.eastweb.prototype.reprojection.GdalTrmmConvert;

public class TrmmReprojectionTest {
    public static void main(String[] args) throws Exception {
        TrmmProjection myt=new  TrmmProjection();
        ProjectInfo projection = Config.getInstance().loadProject("tw_test");
        Projection project=projection.getProjection();


        File input1 = new File("C:\\Users\\general\\Desktop\\testData\\trmm.bin");
        File input2=new File("C:\\Users\\general\\Desktop\\testData\\trmm.tif");
        new GdalTrmmConvert(TrmmProduct.TRMM_3B42,input1,input2).convert();
        File output = new File("C:\\Users\\general\\Desktop\\trmmout.tif");
        myt.project(input2,projection,output);

    }

}
