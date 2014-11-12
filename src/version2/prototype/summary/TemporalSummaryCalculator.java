package version2.prototype.summary;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import edu.sdstate.eastweb.prototype.DataDate;

public class TemporalSummaryCalculator implements SummaryCalculator {

    private File[] inRaster;
    private File inShape;
    private DataDate[] inDate;
    private String outPath;
    private int hrsPerInputData;
    private int hrsPerOutputData;
    private ArrayList<TemporalSummary> tempMethods;
    private ArrayList<MergeSummary> mergMethods;
    private MergeStrategy merStrategy;
    private InterpolateStrategy intStrategy;
    private Calendar projectSDate;
    private CalendarStrategy calStrategy;

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
            int hrsPerOutputData, Calendar projectSDate, CalendarStrategy calStrategy,
            MergeStrategy merStrategy, InterpolateStrategy intStrategy,
            ArrayList<TemporalSummary> tempMethods, ArrayList<MergeSummary> mergMethods) {
        super();
        this.inRaster = inRaster;
        this.inShape = inShape;
        this.inDate = inDate;
        this.outPath = outPath;
        this.hrsPerInputData = hrsPerInputData;
        this.hrsPerOutputData = hrsPerOutputData;
        this.tempMethods = tempMethods;
        this.merStrategy = merStrategy;
        this.intStrategy = intStrategy;
        this.projectSDate = projectSDate;
        this.calStrategy = calStrategy;
        this.mergMethods = mergMethods;
    }

    @Override
    public void calculate() throws Exception {
        // TODO Auto-generated method stub

    }

}
