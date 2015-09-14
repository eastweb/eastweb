package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.database.*;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;
import edu.sdstate.eastweb.prototype.zonalstatistics.*;

public final class UploadResultsTask implements RunnableTask {
    private final ProjectInfo mProject;
    private final EnvironmentalIndex mIndex;
    private final DataDate mDate;

    public UploadResultsTask(ProjectInfo project, EnvironmentalIndex index, DataDate date) {
        mProject = project;
        mIndex = index;
        mDate = date;
    }

    private ResultsUploadMetadata makeMetadata(ZonalSummary zonalSummary) throws IOException {
        final ZonalStatisticsMetadata zonalStatistics = ZonalStatisticsMetadata.fromFile(
                DirectoryLayout.getZonalSummaryMetadata(mProject, mIndex, mDate, zonalSummary.getName()));

        final long timestamp = new java.util.Date().getTime();

        return new ResultsUploadMetadata(zonalStatistics, timestamp);
    }

    private boolean getCanSkipZone(ZonalSummary zonalSummary) {
        try {
            final File file = DirectoryLayout.getDatabaseInsertMetadata(
                    mProject, mIndex, mDate, zonalSummary.getName());
            return ResultsUploadMetadata.fromFile(file)
            .equalsIgnoreTimestamp(makeMetadata(zonalSummary));
        } catch (IOException e) {
            return false;
        }
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

    private List<ZonalSummary> getNeededZones() {
        final List<ZonalSummary> list = new ArrayList<ZonalSummary>();

        for (ZonalSummary zone : mProject.getZonalSummaries()) {
            if (!getCanSkipZone(zone)) {
                list.add(zone);
            }
        }

        return list;
    }

    private static Connection getConnection() throws ConfigReadException, SQLException {
        // Driver Connection Check
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ConfigReadException("Failed to find the PostgreSQL JDBC driver");
        }

        return DriverManager.getConnection(
                Config.getInstance().getDatabaseHost(),
                Config.getInstance().getDatabaseUsername(),
                Config.getInstance().getDatabasePassword()
        );
    }

    @Override
    public void run() throws Exception {
        final List<ZonalSummary> neededZones = getNeededZones();
        for (ZonalSummary zone : neededZones) {
            // TODO: Pool database connections
            final Connection conn = getConnection();
            try {
                // TODO: Dump the summary table to XML as part of the zonal stats task
                //                final SummaryRow[] rows = new ZonalStatisticsArcpyImpl()
                //                .readSummaryTable(
                //                        zone.getField(),
                //                        DirectoryLayout.getZonalSummary(mProject, mIndex, mDate, zone.getName())
                //                        );

                // FIXME: hacked in
                List<SummaryRow> rows = new ArrayList<SummaryRow>();
                BufferedReader reader = new BufferedReader(new FileReader(DirectoryLayout.getZonalSummary(mProject, mIndex, mDate, zone.getName())));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split("[,]");

                    rows.add(
                            new SummaryRow(
                                    values[0],
                                    Double.parseDouble(values[1]),
                                    Double.parseDouble(values[2]),
                                    Double.parseDouble(values[3]),
                                    Double.parseDouble(values[4])
                            )
                    );
                }

                for (SummaryRow row : rows) {
                    new DatabaseManagerOld(conn, mProject).testInsertRow(
                            zone.getShapeFile(),
                            zone.getField(),
                            row.getFieldValue(),
                            mIndex,
                            mDate.getYear(),
                            mDate.getDayOfYear(),
                            row.getCount(),
                            row.getSum(),
                            row.getMean(),
                            row.getStdev()
                    );
                }
            } finally {
                conn.close();
            }
        }

        // Write metadata files
        for (ZonalSummary zone : neededZones) {
            final File file = DirectoryLayout.getDatabaseInsertMetadata(
                    mProject, mIndex, mDate, zone.getName());
            FileUtils.forceMkdir(file.getParentFile());
            makeMetadata(zone).toFile(file);
        }
    }

    @Override
    public String getName() {
        return String.format(
                "Upload results: project=\"%s\", index=%s, date=%s",
                mProject.getName(),
                mIndex,
                mDate.toCompactString()
        );
    }

}