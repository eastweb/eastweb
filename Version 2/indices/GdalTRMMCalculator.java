package edu.sdstate.eastweb.prototype.indices;

import java.io.File;

import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.DirectoryLayout;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;

// FIXME: delete this. temporary (and dreadful) hack so I can release the
// software today
// Simply converts the input to 32 bits and saves it to the output location
public class GdalTRMMCalculator extends IndicesFramework {

    private final int INPUT = 0;

    public GdalTRMMCalculator(ProjectInfo mProject, DataDate mDate, String feature, EnvironmentalIndex mIndex ) throws ConfigReadException {
        setInputFiles(new File[] { DirectoryLayout.getTrmmClip(mProject,
                TrmmProduct.TRMM_3B42, mDate, feature) });
        setOutputFile(DirectoryLayout.getIndex(mProject, mIndex, mDate, feature));
    }

    @Override
    protected double calculatePixelValue(double[] values) throws Exception {
        if (values[INPUT] == 32767) {
            return -3.4028234663852886E38;
        } else {
            return values[INPUT];
        }
    }

}
