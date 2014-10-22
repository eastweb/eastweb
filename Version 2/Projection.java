package edu.sdstate.eastweb.prototype;

import java.io.Serializable;

/**
 * Holds projection information.
 * 
 * @author Isaiah Snell-Feikema
 */
public class Projection implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ProjectionType {
        ALBERS_EQUAL_AREA,
        LAMBERT_CONFORMAL_CONIC,
        TRANSVERSE_MERCATOR
    }

    public enum ResamplingType {
        NEAREST_NEIGHBOR,
        BILINEAR,
        CUBIC_CONVOLUTION
    }

    public enum Datum {
        NAD27,
        NAD83,
        WGS66,
        WGS72,
        WGS84
    }

    public enum Download {
        ftp,
        http
    }

    private ProjectionType projectionType;
    private ResamplingType resamplingType;
    private Datum datum;
    private int pixelSize;
    private double standardParallel1; // AEA and LCC only
    private double standardParallel2; // AEA and LCC only
    private double scalingFactor; // Transverse Mercator only
    private double centralMeridian;
    private double falseEasting;
    private double falseNorthing;
    private double latitudeOfOrigin;


    /**
     * Empty constructor.
     */
    public Projection() {
    }


    /**
     * 
     * 
     * @param projectionType
     * @param resamplingType
     * @param datum
     * @param pixelSize
     * @param semiMajorAxis
     * @param semiMinorAxis
     * @param standardParallel1 only for AEA and LCC projections
     * @param standardParallel2 only AEA and LCC projections
     * @param factor only for transverse Mercator projection
     * @param centralMeridian
     * @param falseEasting
     * @param falseNorthing
     * @param latitudeOfOrigin
     */
    public Projection(
            ProjectionType projectionType,
            ResamplingType resamplingType,
            Datum datum,
            int pixelSize,
            double standardParallel1,
            double standardParallel2,
            double scalingFactor,
            double centralMeridian,
            double falseEasting,
            double falseNorthing,
            double latitudeOfOrigin) {

        this.projectionType = projectionType;
        this.resamplingType = resamplingType;
        this.datum = datum;
        this.pixelSize = pixelSize;
        this.standardParallel1 = standardParallel1;
        this.standardParallel2 = standardParallel2;
        this.scalingFactor = scalingFactor;
        this.centralMeridian = centralMeridian;
        this.falseEasting = falseEasting;
        this.falseNorthing = falseNorthing;
        this.latitudeOfOrigin = latitudeOfOrigin;
    }

    public Projection(Projection other) {
        projectionType = other.projectionType;
        resamplingType = other.resamplingType;
        datum = other.datum;
        pixelSize = other.pixelSize;
        standardParallel1 = other.standardParallel1;
        standardParallel2 = other.standardParallel2;
        scalingFactor = other.scalingFactor;
        centralMeridian = other.centralMeridian;
        falseEasting = other.falseEasting;
        falseNorthing = other.falseNorthing;
        latitudeOfOrigin = other.latitudeOfOrigin;
    }

    public ProjectionType getProjectionType() {
        return projectionType;
    }

    public void setProjectionType(ProjectionType projectionType) {
        this.projectionType = projectionType;
    }

    public ResamplingType getResamplingType() {
        return resamplingType;
    }

    public void setResamplingType(ResamplingType resamplingType) {
        this.resamplingType = resamplingType;
    }

    public Datum getDatum() {
        return datum;
    }

    public void setDatum(Datum datum) {
        this.datum = datum;
    }

    public int getPixelSize() {
        return pixelSize;
    }

    public void setPixelSize(int pixelSize) {
        this.pixelSize = pixelSize;
    }

    public double getStandardParallel1() {
        return standardParallel1;
    }

    public void setStandardParallel1(double standardParallel1) {
        this.standardParallel1 = standardParallel1;
    }

    public double getStandardParallel2() {
        return standardParallel2;
    }

    public void setStandardParallel2(double standardParallel2) {
        this.standardParallel2 = standardParallel2;
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(double scalingFactor) {
        this.scalingFactor = scalingFactor;
    }

    public double getCentralMeridian() {
        return centralMeridian;
    }

    public void setCentralMeridian(double centralMeridian) {
        this.centralMeridian = centralMeridian;
    }

    public double getFalseEasting() {
        return falseEasting;
    }

    public void setFalseEasting(double falseEasting) {
        this.falseEasting = falseEasting;
    }

    public double getFalseNorthing() {
        return falseNorthing;
    }

    public void setFalseNorthing(double falseNorthing) {
        this.falseNorthing = falseNorthing;
    }

    public double getLatitudeOfOrigin() {
        return latitudeOfOrigin;
    }

    public void setLatitudeOfOrigin(double latitudeOfOrigin) {
        this.latitudeOfOrigin = latitudeOfOrigin;
    }


    @Override
    public String toString() {
        return new StringBuilder()
        .append("{projection type: ").append(projectionType)
        .append(", resampling type: ").append(resamplingType)
        .append(", datum: ").append(datum)
        .append(", pixel size: ").append(Integer.toString(pixelSize))
        .append(", standard parallel 1: ").append(Double.toString(standardParallel1))
        .append(", standard parallel 2: ").append(Double.toString(standardParallel2))
        .append(", scaling factor: ").append(Double.toString(scalingFactor))
        .append(", central meridian: ").append(Double.toString(centralMeridian))
        .append(", false northing: ").append(Double.toString(falseEasting))
        .append(", false easting: ").append(Double.toString(falseNorthing))
        .append(", latitude of origin: ").append(Double.toString(latitudeOfOrigin))
        .append("}").toString();
    }

}
