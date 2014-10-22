package edu.sdstate.eastweb.prototype.reprojection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.Projection;
import edu.sdstate.eastweb.prototype.Projection.Datum;
import edu.sdstate.eastweb.prototype.Projection.ProjectionType;
import edu.sdstate.eastweb.prototype.Projection.ResamplingType;

/**
 * Abstract class for reprojection modules that interact with ArcGIS 10.
 * 
 * @author Isaiah Snell-Feikema
 */
public abstract class ArcGisReprojection {

    private static final String NAD27_SPHEROID_NAME = "Clarke_1866";
    private static final double NAD27_SPHEROID_A = 6378137.0;
    private static final double NAD27_SPHEROID_B = 294.9786982;

    private static final String NAD83_SPHEROID_NAME = "GRS_1980";
    private static final double NAD83_SPHEROID_A = 6378137.0;
    private static final double NAD83_SPHEROID_B = 298.257222101;

    private static final String WGS66_SPHEROID_NAME = "WGS_1966";
    private static final double WGS66_SPHEROID_A = 6378145;
    private static final double WGS66_SPHEROID_B = 298.25;

    private static final String WGS72_SPHEROID_NAME = "WGS_1972";
    private static final double WGS72_SPHEROID_A = 6378135.0;
    private static final double WGS72_SPHEROID_B = 298.26;

    private static final String WGS84_SPHEROID_NAME = "WGS_1984";
    private static final double WGS84_SPHEROID_A = 6378137.0;
    private static final double WGS84_SPHEROID_B = 298.257223563;
    private static File tempDictionary;

    /**
     * Writes a projection object to an ArcGIS 10 projection file (*.prj).
     * 
     * @param projection
     * @return the projection file
     */
    protected File createProjectionFile(Projection projection) throws IOException {
        tempDictionary=new File(Config.getInstance().getTempDirectory());
        File projectionFile = File.createTempFile("reprojection", ".prj",tempDictionary);
        projectionFile.deleteOnExit();

        ProjNode projcs = new ProjNode("PROJCS");

        // Add name
        if (projection.getProjectionType() == ProjectionType.ALBERS_EQUAL_AREA) {
            projcs.add("Albers_Equal_Area");
        } else if (projection.getProjectionType() == ProjectionType.LAMBERT_CONFORMAL_CONIC) {
            projcs.add("Lambert_Conformal_Conic");
        } else if (projection.getProjectionType() == ProjectionType.TRANSVERSE_MERCATOR) {
            projcs.add("Transverse_Mercator");
        }

        // Add geographic coordinate system
        ProjNode geogcs = new ProjNode("GEOGCS");
        ProjNode datum = new ProjNode("DATUM");
        ProjNode spheroid = new ProjNode("SPHEROID");
        if (projection.getDatum() == Projection.Datum.NAD27) {
            geogcs.add("GCS_North_American_1927");
            datum.add("D_North_American_1927");
            spheroid.add(NAD27_SPHEROID_NAME);
            spheroid.add(NAD27_SPHEROID_A);
            spheroid.add(NAD27_SPHEROID_B);
        } else if (projection.getDatum() == Datum.NAD83) {
            geogcs.add("GCS_North_American_1983");
            datum.add("D_North_American_1983");
            spheroid.add(NAD83_SPHEROID_NAME);
            spheroid.add(NAD83_SPHEROID_A);
            spheroid.add(NAD83_SPHEROID_B);
        } else if (projection.getDatum() == Datum.WGS66) {
            geogcs.add("GCS_WGS_1966");
            datum.add("D_WGS_1966");
            spheroid.add(WGS66_SPHEROID_NAME);
            spheroid.add(WGS66_SPHEROID_A);
            spheroid.add(WGS66_SPHEROID_B);
        } else if (projection.getDatum() == Datum.WGS72) {
            geogcs.add("GCS_WGS_1972");
            datum.add("D_WGS_1972");
            spheroid.add(WGS72_SPHEROID_NAME);
            spheroid.add(WGS72_SPHEROID_A);
            spheroid.add(WGS72_SPHEROID_B);
        } else if (projection.getDatum() == Datum.WGS84) {
            geogcs.add("GCS_WGS_1984");
            datum.add("D_WGS_1984");
            spheroid.add(WGS84_SPHEROID_NAME);
            spheroid.add(WGS84_SPHEROID_A);
            spheroid.add(WGS84_SPHEROID_B);
        }
        datum.add(spheroid);
        geogcs.add(datum);

        // Add prime meridian
        ProjNode primeMeridian = new ProjNode("PRIMEM");
        primeMeridian.add("Greenwich");
        primeMeridian.add(0.0);
        geogcs.add(primeMeridian);

        // Add unit
        ProjNode unitDegree = new ProjNode("UNIT");
        unitDegree.add("Degree");
        unitDegree.add(0.0174532925199433); // FIXME: check!
        geogcs.add(unitDegree);

        projcs.add(geogcs);


        // Add projection
        ProjNode proj = new ProjNode("PROJECTION");
        if (projection.getProjectionType() == ProjectionType.ALBERS_EQUAL_AREA) {
            proj.add("Albers");
        } else if (projection.getProjectionType() == ProjectionType.LAMBERT_CONFORMAL_CONIC) {
            proj.add("Lambert_Conformal_Conic");
        } else if (projection.getProjectionType() == ProjectionType.TRANSVERSE_MERCATOR) {
            proj.add("Transverse_Mercator");
        }
        projcs.add(proj);


        // Add false easting
        ProjNode falseEasting = new ProjNode("PARAMETER");
        falseEasting.add("false_easting");
        falseEasting.add(projection.getFalseEasting());
        projcs.add(falseEasting);

        // Add false northing
        ProjNode falseNorthing = new ProjNode("PARAMETER");
        falseNorthing.add("false_northing");
        falseNorthing.add(projection.getFalseNorthing());
        projcs.add(falseNorthing);

        // Add central meridian
        ProjNode centralMeridian = new ProjNode("PARAMETER");
        centralMeridian.add("central_meridian");
        centralMeridian.add(projection.getCentralMeridian());
        projcs.add(centralMeridian);

        if (projection.getProjectionType() == ProjectionType.ALBERS_EQUAL_AREA ||
                projection.getProjectionType() == ProjectionType.LAMBERT_CONFORMAL_CONIC) {

            // Add standard parallel 1
            ProjNode standardParallel1 = new ProjNode("PARAMETER");
            standardParallel1.add("standard_parallel_1");
            standardParallel1.add(projection.getStandardParallel1());
            projcs.add(standardParallel1);

            // Add standard parallel 2
            ProjNode standardParallel2 = new ProjNode("PARAMETER");
            standardParallel2.add("standard_parallel_2");
            standardParallel2.add(projection.getStandardParallel2());
            projcs.add(standardParallel2);
        }
        else if (projection.getProjectionType() == ProjectionType.TRANSVERSE_MERCATOR) {

            // Add scale factor
            ProjNode scaleFactor = new ProjNode("PARAMETER");
            scaleFactor.add("scale_factor");
            scaleFactor.add(projection.getScalingFactor());
            projcs.add(scaleFactor);
        }

        // Add latitude of origin
        ProjNode originOfLatitude = new ProjNode("PARAMETER");
        originOfLatitude.add("latitude_of_origin");
        originOfLatitude.add(projection.getLatitudeOfOrigin());
        projcs.add(originOfLatitude);

        // Add unit
        ProjNode unitMeter = new ProjNode("UNIT");
        unitMeter.add("Meter");
        unitMeter.add(1.0);
        projcs.add(unitMeter);

        // Write to projection file
        PrintWriter out =
            new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(projectionFile)));

        out.print(projcs.toString());
        out.close();

        return projectionFile;
    }


    /**
     * Translates resampling types to their ArcPy equivalent.
     * 
     * @param resamplingType
     * @return ArcPy equivalent of the resampling type
     * @throws Exception
     */
    protected String translateResamplingType(ResamplingType resamplingType) {
        if (resamplingType == ResamplingType.NEAREST_NEIGHBOR) {
            return "NEAREST";
        } else if (resamplingType == ResamplingType.CUBIC_CONVOLUTION) {
            return "CUBIC";
        } else if (resamplingType == ResamplingType.BILINEAR) {
            return "BILINEAR";
        } else {
            throw new IllegalArgumentException("Unsupported resampling type!");
        }
    }

}
