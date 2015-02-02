package version2.prototype.summary;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;

import edu.sdstate.eastweb.prototype.DataDate;

public class SummaryData {
    // ZonalSummaryCalculator variables
    public File inRaster;
    public File inShape;
    public File outTableFile;
    public String zoneField;
    public SummariesCollection summaries;

    // Remaining TemporalSummaryCalculator variables
    public DataDate[] inDate;
    public int hrsPerInputData;
    public int hrsPerOutputData;
    public ArrayList<TemporalSummary> tempMethods;
    public ArrayList<MergeSummary> mergMethods;
    public MergeStrategy merStrategy;
    public Calendar projectSDate;
    public CalendarStrategy calStrategy;

    /**
     * <p>Accepts values for all inputs.</p>
     *
     * @param inRaster - Type: File[] - A File object for each DataDate.<br/>
     * Example:  <code>{@link #version2.prototype.DirectoryLayout.getIndexMetadata(ProjectInfo, String, DataDate, String)}<br/>
     * getIndexMetadata(mProject, mIndex, sDate, zone.getShapeFile())</code>
     * @param inShape - Type: File - The layer/shape file.<br/>
     * Example:  <code>File({@link #version2.prototype.DirectoryLayout.getSettingsDirectory(ProjectInfo)},
     * {@link #version2.prototype.ZonalSummary.getShapeFile()})<br/>
     * File(DirectoryLayout.getSettingsDirectory(mProject), zone.getShapeFile())</code>
     * @param outTable - Type: File - File object pointing to output location for zonal summary
     * @param zone - Type: String - The zone name.<br/>
     * Example:  <code>for ({@link version2.prototype.ZonalSummary ZonalSummary} zone : mProject.{@link #version2.prototype.ProjectInfo.getSummaries()}) { zone.{@link #version2.prototype.ZonalSummary.getField()}; }<br/>
     * for (ZonalSummary zone : mProject.getSummaries())<br/>  { zone.getField(); }</code>
     * @param summarySingletonNames - Type: ArrayList<String> - A list of the class names of the summaries to use in zonal summary.<br/>
     * @param inDate - Type: DataDate[] - An array of the dates of the downloaded data to be used in finding the data in the file system and in processing temporal summaries.<br/>
     * @param hrsPerInputData - Type: int - The number of hours each piece of downloaded data represents.
     * @param hrsPerOutputData - Type: int - The number of hours each piece of summary/output data will represent.
     * @param projectSDate - Type: Calendar - The projects start date.
     * @param calStrategy - Type: CalendarStrategy - The strategy to use when getting the starting date of the week.
     * @param merStrategy - Type: MergeStrategy - The strategy to use when merging downloaded data.
     * @param tempMethods - Type: ArrayList<TemporalSummary> - The list of summary methods to calculate for temporal summary.
     * @param mergMethods - Type: ArrayList<MergeSummary> - The list of summary methods to use during merging with the chosen MergeStrategy.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public SummaryData(File inRaster, File inShape, File outTable, String zone,
            ArrayList<String> summarySingletonNames, DataDate[] inDate, int hrsPerInputData,
            int hrsPerOutputData, Calendar projectSDate, CalendarStrategy calStrategy,
            MergeStrategy merStrategy, ArrayList<TemporalSummary> tempMethods,
            ArrayList<MergeSummary> mergMethods) throws ClassNotFoundException, NoSuchMethodException,
            SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        this.inRaster = inRaster;
        this.inShape = inShape;
        outTableFile = outTable;
        zoneField = zone;
        summaries = new SummariesCollection(summarySingletonNames);
        this.inDate = inDate;
        this.hrsPerInputData = hrsPerInputData;     // set to -1 when not used
        this.hrsPerOutputData = hrsPerOutputData;   // set to -1 when not used
        this.tempMethods = tempMethods;
        this.merStrategy = merStrategy;
        this.projectSDate = projectSDate;
        this.calStrategy = calStrategy;
        this.mergMethods = mergMethods;
    }
}
