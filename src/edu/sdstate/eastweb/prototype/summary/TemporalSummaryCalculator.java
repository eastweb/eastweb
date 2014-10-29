package edu.sdstate.eastweb.prototype.summary;

import java.io.File;

import edu.sdstate.eastweb.prototype.DataDate;

public class TemporalSummaryCalculator implements SummaryCalculator {

    private File[] inRaster;
    private File inShape;
    private DataDate[] inDate;
    private String outPath;
    private int hrsPerInputData;
    private int hrsPerOutputData;
    private SummaryStrategy[] sumStrategy;
    private MergeStrategy merStrategy;
    private InterpolateStrategy intStrategy;

    /**
     * @param inRaster
     * @param inShape
     * @param inDate
     * @param outPath
     * @param hrsPerInputData
     * @param hrsPerOutputData
     * @param sumStrategy
     * @param merStrategy
     * @param intStrategy
     */
    public TemporalSummaryCalculator(File[] inRaster, File inShape,
            DataDate[] inDate, String outPath, int hrsPerInputData,
            int hrsPerOutputData, SummaryStrategy[] sumStrategy,
            MergeStrategy merStrategy, InterpolateStrategy intStrategy) {
        super();
        this.inRaster = inRaster;
        this.inShape = inShape;
        this.inDate = inDate;
        this.outPath = outPath;
        this.hrsPerInputData = hrsPerInputData;
        this.hrsPerOutputData = hrsPerOutputData;
        this.sumStrategy = sumStrategy;
        this.merStrategy = merStrategy;
        this.intStrategy = intStrategy;
    }

    @Override
    public void calculate() throws Exception {
        // TODO Auto-generated method stub

    }

}
