package edu.sdstate.eastweb.prototype.database;

import java.sql.*;
import java.util.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;

public class DatabaseManagerOld {
    private final Connection mConn;
    private final String mSchemaName;

    public DatabaseManagerOld(Connection conn, ProjectInfo project) {
        mConn = conn;
        mSchemaName = getSchemaName(project);
    }

    /**
     * Gets the name of the specified project's database schema.
     * The returned name does not need to be quoted to use in SQL.
     */
    public static String getSchemaName(ProjectInfo project) {
        final String name = project.getName();
        final StringBuilder builder = new StringBuilder("project_");
        for (int index = 0; index < name.length(); ) {
            final int codePointIn = name.codePointAt(index);
            index += Character.charCount(codePointIn);

            final int codePointOut;
            if (codePointIn >= 'A' && codePointIn <= 'Z') {
                // Convert to upper-case letters to lower-case
                codePointOut = codePointIn - 'A' + 'a';
            } else if ((codePointIn >= 'a' && codePointIn <= 'z') ||
                    (codePointIn >= '0' && codePointIn <= '9')) {
                // Preserve lower-case letters and digits
                codePointOut = codePointIn;
            } else {
                // Make anything else an underscore
                codePointOut = '_';
            }
            builder.appendCodePoint(codePointOut);
        }
        return builder.toString();
    }

    /**
     * Gets the name of the specified project's database schema.
     * The returned name does not need to be quoted to use in SQL.
     */
    public static String getSchemaName(String project) {
        final String name = project;
        final StringBuilder builder = new StringBuilder("project_");
        for (int index = 0; index < name.length(); ) {
            final int codePointIn = name.codePointAt(index);
            index += Character.charCount(codePointIn);

            final int codePointOut;
            if (codePointIn >= 'A' && codePointIn <= 'Z') {
                // Convert to upper-case letters to lower-case
                codePointOut = codePointIn - 'A' + 'a';
            } else if ((codePointIn >= 'a' && codePointIn <= 'z') ||
                    (codePointIn >= '0' && codePointIn <= '9')) {
                // Preserve lower-case letters and digits
                codePointOut = codePointIn;
            } else {
                // Make anything else an underscore
                codePointOut = '_';
            }
            builder.appendCodePoint(codePointOut);
        }
        return builder.toString();
    }

    /**
     * Creates a schema and some tables for a project.
     * <b>This method will DROP an existing schema with the same name!</b>
     * @param conn Database connection
     * @param project Project
     * @throws SQLException
     */
    public void recreateSchema() throws SQLException {
        // Drop an existing schema with the same name
        mConn.prepareStatement(String.format(
                "DROP SCHEMA IF EXISTS \"%s\" CASCADE",
                mSchemaName
        )).executeUpdate();

        // Create the schema for this project
        mConn.prepareStatement(String.format(
                "CREATE SCHEMA \"%s\"",
                mSchemaName
        )).executeUpdate();

        // Create the ZoneFields table
        mConn.prepareStatement(String.format(
                "CREATE TABLE \"%1$s\".\"ZoneFields\"\n" +
                "(\n" +
                "  \"zoneFieldID\" integer NOT NULL,\n" +
                "  \"shapefile\" text NOT NULL,\n" +
                "  \"field\" text NOT NULL,\n" +
                "  CONSTRAINT \"pk_ZoneFields\"\n" +
                "      PRIMARY KEY (\"zoneFieldID\")\n" +
                ")",
                mSchemaName
        )).executeUpdate();

        // Create the Zones table
        mConn.prepareStatement(String.format(
                "CREATE TABLE \"%1$s\".\"Zones\"\n" +
                "(\n" +
                "  \"zoneID\" integer NOT NULL,\n" +
                "  \"zoneFieldID\" integer NOT NULL,\n" +
                "  \"name\" text NOT NULL,\n" +
                "  CONSTRAINT \"pk_Zones\"\n" +
                "      PRIMARY KEY (\"zoneID\"),\n" +
                "  CONSTRAINT \"fk_Zones\"\n" +
                "      FOREIGN KEY (\"zoneFieldID\")\n" +
                "      REFERENCES \"%1$s\".\"ZoneFields\" (\"zoneFieldID\")\n" +
                ")",
                mSchemaName
        )).executeUpdate();

        // Create an index for the Zones table's foreign key
        mConn.prepareStatement(String.format(
                "CREATE INDEX \"fki_Zones\" ON \"%1$s\".\"Zones\"(\"zoneFieldID\")",
                mSchemaName
        )).executeUpdate();

        // Create the ZonalStats table
        mConn.prepareStatement(String.format(
                "CREATE TABLE \"%1$s\".\"ZonalStats\"\n" +
                "(\n" +
                "  \"index\" integer NOT NULL,\n" +
                "  \"year\" integer NOT NULL,\n" +
                "  \"day\" integer NOT NULL,\n" +
                "  \"zoneID\" integer NOT NULL,\n" +
                "  \"count\" double precision NOT NULL,\n" +
                "  \"sum\" double precision NOT NULL,\n" +
                "  \"mean\" double precision NOT NULL,\n" +
                "  \"stdev\" double precision NOT NULL,\n" +
                "  CONSTRAINT \"pk_ZonalStats\"\n" +
                "      PRIMARY KEY (\"index\", \"year\", \"day\", \"zoneID\"),\n" +
                "  CONSTRAINT \"fk_ZonalStats\"\n" +
                "      FOREIGN KEY (\"zoneID\")\n" +
                "      REFERENCES \"%1$s\".\"Zones\" (\"zoneID\")\n" +
                ")",
                mSchemaName
        )).executeUpdate();

        // Create an index for the ZonalStats table's foreign key
        mConn.prepareStatement(String.format(
                "CREATE INDEX \"fki_ZonalStats\" ON \"%1$s\".\"ZonalStats\"(\"zoneID\")",
                mSchemaName
        )).executeUpdate();
    }

    /**
     * Looks up the zoneFieldID for the specified (shapefile, field) pair.
     * Returns null if there is no matching record.
     * @throws SQLException
     */
    public Integer lookupZoneFieldId(String shapefile, String field) throws SQLException {
        final PreparedStatement ps = mConn.prepareStatement(String.format(
                "SELECT \"zoneFieldID\"\n" +
                "FROM \"%1$s\".\"ZoneFields\"\n" +
                "WHERE \"shapefile\" = ? AND \"field\" = ?",
                mSchemaName
        ));
        ps.setString(1, shapefile);
        ps.setString(2, field);

        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return null;
        }
    }

    /**
     * Gets the next available zoneFieldID.
     * @throws SQLException
     */
    public int nextZoneFieldId() throws SQLException {
        final ResultSet rs = mConn.prepareStatement(String.format(
                "SELECT MAX(\"zoneFieldID\")\n" +
                "FROM \"%1$s\".\"ZoneFields\"",
                mSchemaName
        )).executeQuery();

        if (rs.next()) {
            return rs.getInt(1) + 1;
        } else {
            return 0;
        }
    }

    /**
     * Looks up the zoneFieldID for the specified (shapefile, field) pair.
     * A new zoneFieldID is created if there is no matching record.
     * Do this in a transaction!
     * @throws SQLException
     */
    public int getZoneFieldId(String shapefile, String field) throws SQLException {
        Integer lookup = lookupZoneFieldId(shapefile, field);
        if (lookup != null) {
            return lookup;
        }

        final int zoneFieldId = nextZoneFieldId();

        final PreparedStatement ps = mConn.prepareStatement(String.format(
                "INSERT INTO \"%1$s\".\"ZoneFields\" (\n" +
                "  \"zoneFieldID\",\n" +
                "  \"shapefile\",\n" +
                "  \"field\"\n" +
                ") VALUES (\n" +
                "  ?,\n" +
                "  ?,\n" +
                "  ?\n" +
                ")",
                mSchemaName
        ));
        ps.setInt(1, zoneFieldId);
        ps.setString(2, shapefile);
        ps.setString(3, field);
        ps.executeUpdate();

        return zoneFieldId;
    }

    /**
     * Looks up the ZoneID for the specified (zoneFieldID, zone) pair.
     * Returns null if there is no matching record.
     * @throws SQLException
     */
    public Integer lookupZoneId(int zoneFieldId, String zone) throws SQLException {
        final PreparedStatement ps = mConn.prepareStatement(String.format(
                "SELECT \"zoneID\"\n" +
                "FROM \"%1$s\".\"Zones\"\n" +
                "WHERE \"zoneFieldID\" = ? AND \"name\" = ?",
                mSchemaName
        ));
        ps.setInt(1, zoneFieldId);
        ps.setString(2, zone);

        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return null;
        }
    }

    /**
     * Gets the next available zoneID.
     * @throws SQLException
     */
    public int nextZoneId() throws SQLException {
        final ResultSet rs = mConn.prepareStatement(String.format(
                "SELECT MAX(\"zoneID\")\n" +
                "FROM \"%1$s\".\"Zones\"",
                mSchemaName
        )).executeQuery();

        if (rs.next()) {
            return rs.getInt(1) + 1;
        } else {
            return 0;
        }
    }

    /**
     * Looks up the zoneID for the specified (zoneFieldID, zone) pair.
     * A new zoneID is created if there is no matching record.
     * Do this in a transaction!
     * @throws SQLException
     */
    public int getZoneId(int zoneFieldId, String zone) throws SQLException {
        Integer lookup = lookupZoneId(zoneFieldId, zone);
        if (lookup != null) {
            return lookup;
        }

        final int zoneId = nextZoneId();

        final PreparedStatement ps = mConn.prepareStatement(String.format(
                "INSERT INTO \"%1$s\".\"Zones\" (\n" +
                "  \"zoneID\",\n" +
                "  \"zoneFieldID\",\n" +
                "  \"name\"\n" +
                ") VALUES (\n" +
                "  ?,\n" +
                "  ?,\n" +
                "  ?\n" +
                ")",
                mSchemaName
        ));
        ps.setInt(1, zoneId);
        ps.setInt(2, zoneFieldId);
        ps.setString(3, zone);
        ps.executeUpdate();

        return zoneId;
    }

    /**
     * Creates records as needed to insert the specified zonal stats row.
     * @throws SQLException
     */
    public void testInsertRow(String shapefile, String field, String zone, EnvironmentalIndex index,
            int year, int day, double count, double sum, double mean, double stdev)
    throws SQLException
    {
        final boolean previousAutoCommit = mConn.getAutoCommit();
        mConn.setAutoCommit(false);
        try {
            mConn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            final int zoneId = getZoneId(getZoneFieldId(shapefile, field), zone);

            final PreparedStatement psExists = mConn.prepareStatement(String.format(
                    "SELECT COUNT(*)\n" +
                    "FROM \"%1$s\".\"ZonalStats\"\n" +
                    "WHERE\n" +
                    "  \"index\" = ? AND\n" +
                    "  \"year\" = ? AND\n" +
                    "  \"day\" = ? AND\n" +
                    "  \"zoneID\" = ?\n",
                    mSchemaName
            ));
            psExists.setInt(1, index.ordinal());
            psExists.setInt(2, year);
            psExists.setInt(3, day);
            psExists.setInt(4, zoneId);
            final ResultSet rsExists = psExists.executeQuery();
            final int numMatchingRows;
            try {
                if (!rsExists.next()) {
                    throw new SQLException("Expected one result row");
                }
                numMatchingRows = rsExists.getInt(1);
            } finally {
                rsExists.close();
            }

            if (numMatchingRows == 0) {
                final PreparedStatement psInsert = mConn.prepareStatement(String.format(
                        "INSERT INTO \"%1$s\".\"ZonalStats\" (\n" +
                        "  \"index\",\n" +
                        "  \"year\",\n" +
                        "  \"day\",\n" +
                        "  \"zoneID\",\n" +
                        "  \"count\",\n" +
                        "  \"sum\",\n" +
                        "  \"mean\",\n" +
                        "  \"stdev\"\n" +
                        ") VALUES (\n" +
                        "  ?,\n" +
                        "  ?,\n" +
                        "  ?,\n" +
                        "  ?,\n" +
                        "  ?,\n" +
                        "  ?,\n" +
                        "  ?,\n" +
                        "  ?\n" +
                        ")",
                        mSchemaName
                ));
                psInsert.setInt(1, index.ordinal());
                psInsert.setInt(2, year);
                psInsert.setInt(3, day);
                psInsert.setInt(4, zoneId);
                psInsert.setDouble(5, count);
                psInsert.setDouble(6, sum);
                psInsert.setDouble(7, mean);
                psInsert.setDouble(8, stdev);
                psInsert.executeUpdate();
            } else {
                final PreparedStatement psUpdate = mConn.prepareStatement(String.format(
                        "UPDATE \"%1$s\".\"ZonalStats\"\n" +
                        "SET\n" +
                        "  \"count\" = ?,\n" +
                        "  \"sum\" = ?,\n" +
                        "  \"mean\" = ?,\n" +
                        "  \"stdev\" = ?\n" +
                        "WHERE\n" +
                        "  \"index\" = ? AND\n" +
                        "  \"year\" = ? AND\n" +
                        "  \"day\" = ? AND\n" +
                        "  \"zoneID\" = ?\n",
                        mSchemaName
                ));
                psUpdate.setDouble(1, count);
                psUpdate.setDouble(2, sum);
                psUpdate.setDouble(3, mean);
                psUpdate.setDouble(4, stdev);
                psUpdate.setInt(5, index.ordinal());
                psUpdate.setInt(6, year);
                psUpdate.setInt(7, day);
                psUpdate.setInt(8, zoneId);
                psUpdate.executeUpdate();
            }

            mConn.commit();
        } catch (SQLException e) {
            mConn.rollback();
            throw e;
        } finally {
            mConn.setAutoCommit(previousAutoCommit);
        }
    }

    private static void testCreateFakeData(DatabaseManagerOld mgr) throws SQLException {
        mgr.recreateSchema();

        // Fake data -- normally this would come from the project's shapefiles
        final String[] SHAPEFILES = new String[] { "NGP_AEA.shp", "some_other.shp" };
        final Map<String, String[]> zonesByField = new HashMap<String, String[]>();
        zonesByField.put("FIPS", new String[] { "46011", "46077", "46057", "46039" });
        zonesByField.put("County", new String[] { "Brookings", "Kingsbury", "Hamlin", "Deuel" });

        // Insert a few rows with random numbers
        for (final String shapefile : SHAPEFILES) {
            for (final Map.Entry<String, String[]> fieldEntry : zonesByField.entrySet()) {
                final String field = fieldEntry.getKey();
                for (final String zone : fieldEntry.getValue()) {

                    DataDate date = new DataDate(360, 2010);
                    for (int dateCounter = 0; dateCounter < 5; ++dateCounter) {
                        final double count = Math.random();
                        final double sum = Math.random();
                        final double mean = Math.random();
                        final double stdev = Math.random();
                        mgr.testInsertRow(shapefile, field, zone, EnvironmentalIndex.NDVI,
                                date.getYear(), date.getDayOfYear(), count, sum, mean, stdev);

                        date = date.next(8);
                    }

                }
            }
        }
    }


}
