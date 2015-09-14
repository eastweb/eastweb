package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;
import edu.sdstate.eastweb.prototype.indices.IndexMetadata;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;
import edu.sdstate.eastweb.prototype.zonalstatistics.*;

public class CalculateZonalStatisticsTask implements RunnableTask {
    private static final long serialVersionUID = 1L;
    private final ProjectInfo mProject;
    private final EnvironmentalIndex mIndex;
    private final DataDate mDate;

    public CalculateZonalStatisticsTask(ProjectInfo project, EnvironmentalIndex index, DataDate date) {
        mProject = project;
        mIndex = index;
        mDate = date;
    }

    private ZonalStatisticsMetadata makeMetadata(ZonalSummary zone) throws IOException {
        final IndexMetadata index = IndexMetadata.fromFile(
                DirectoryLayout.getIndexMetadata(mProject, mIndex, mDate, zone.getShapeFile())); // FIXME

        final long timestamp = new Date().getTime();

        return new ZonalStatisticsMetadata(index, zone.getName(), timestamp);
    }

    private boolean getCanSkipZone(ZonalSummary zone) {
        try {
            final File file = DirectoryLayout.getZonalSummaryMetadata(
                    mProject, mIndex, mDate, zone.getName());
            return ZonalStatisticsMetadata.fromFile(file)
            .equalsIgnoreTimestamp(makeMetadata(zone));
        } catch (IOException e) {
            return false;
        }
    }

    private List<ZonalSummary> getNeededZonalSummaries() {
        final List<ZonalSummary> list = new ArrayList<ZonalSummary>();

        for (ZonalSummary zone : mProject.getZonalSummaries()) {
            if (!getCanSkipZone(zone)) {
                list.add(zone);
            }
        }

        return list;
    }

    @Override
    public boolean getCanSkip() {
        for (ZonalSummary zone : mProject.getZonalSummaries()) {
            if (!getCanSkipZone(zone)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void run() throws Exception {
        for (ZonalSummary zone : getNeededZonalSummaries()) {
            final File inRaster = DirectoryLayout.getIndex(mProject, mIndex, mDate, zone.getShapeFile());
            final File inShapefile = new File(DirectoryLayout.getSettingsDirectory(mProject),
                    zone.getShapeFile());
            final File outTable = DirectoryLayout.getZonalSummary(
                    mProject, mIndex, mDate, zone.getName());

            FileUtils.forceMkdir(outTable.getParentFile());

            GdalZonalStatistics zonalStatistics = new GdalZonalStatistics(
                    inRaster, inShapefile, zone.getField(), outTable);
            zonalStatistics.calculate();
        }

        // Write metadata files
        for (ZonalSummary zone : getNeededZonalSummaries()) {
            final File file = DirectoryLayout.getZonalSummaryMetadata(
                    mProject, mIndex, mDate, zone.getName());
            FileUtils.forceMkdir(file.getParentFile());
            makeMetadata(zone).toFile(file);
        }
    }

    @Override
    public String getName() {
        return String.format(
                "Calculate zonal statistics: project=\"%s\", index=%s, date=%s",
                mProject.getName(),
                mIndex,
                mDate.toCompactString()
        );
    }

}