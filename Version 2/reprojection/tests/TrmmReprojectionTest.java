package edu.sdstate.eastweb.prototype.reprojection.tests;

import java.io.File;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.reprojection.NldasProjection;
import edu.sdstate.eastweb.prototype.reprojection.TrmmProjection;
import edu.sdstate.eastweb.prototype.reprojection.GdalTrmmConvert;

public class TrmmReprojectionTest {
    public static void main(String[] args) throws Exception {
        NldasProjection myt = new NldasProjection();
        ProjectInfo projection = Config.getInstance().loadProject("tw_test");
        // Projection project=projection.getProjection();

        File input1 =
            new File("E:\\eastweb-data\\download\\Nldas\\2013\\238\\Nldas.gbr");

        //new GdalTrmmConvert(TrmmProduct.TRMM_3B42, input1, input2).convert();
        File output = new File("E:\\eastweb-data\\projects\\tw_test\\reprojected\\Nldas\\2013\\238\\Nldas.tif");
        myt.project(input1, projection, output);

    }

}
