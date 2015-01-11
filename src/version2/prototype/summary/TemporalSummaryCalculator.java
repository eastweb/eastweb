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
    public TemporalSummaryCalculator(SummaryData data)
    {
        //inRaster = data.inRaster;
        inShape = data.inShape;
        inDate = data.inDate;
        outPath = data.outTableFile.getPath();
        hrsPerInputData = data.hrsPerInputData;
        hrsPerOutputData = data.hrsPerOutputData;
        tempMethods = data.tempMethods;
        merStrategy = data.merStrategy;
        projectSDate = data.projectSDate;
        calStrategy = data.calStrategy;
        mergMethods = data.mergMethods;
    }

    @Override
    public void run() throws Exception {
        // TODO Auto-generated method stub

    }

}
