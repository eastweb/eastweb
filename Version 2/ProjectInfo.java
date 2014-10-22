package edu.sdstate.eastweb.prototype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private boolean active;
    private String name;
    private DataDate startDate;
    private String watermask;
    private String elevation;
    private double minLst;
    private double maxLst;
    private Projection projection;
    private ModisTile[] modisTiles;
    private ZonalSummary[] zonalSummaries;
    private boolean calculateETa;

    public ProjectInfo() {
        projection = new Projection();
    }

    public ProjectInfo(String name, DataDate startDate, String watermask,
            String elevation, double minLst, double maxLst,
            Projection projection, ModisTile[] modisTiles,
            ZonalSummary[] zonalSummaries, boolean calculateETa) {
        this.name = name;
        this.startDate = startDate;
        this.watermask = watermask;
        this.elevation = elevation;
        this.minLst = minLst;
        this.maxLst = maxLst;
        this.projection = projection;
        this.modisTiles = modisTiles.clone();           // Prevent modification
        this.zonalSummaries = zonalSummaries.clone();   // by cloning.
        this.calculateETa = calculateETa;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStartDate(DataDate startDate) {
        this.startDate = startDate;
    }

    public DataDate getStartDate() {
        return startDate;
    }

    public void setWatermask(String watermask) {
        this.watermask = watermask;
    }

    public String getWatermask() {
        return watermask;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public String getElevation() {
        return elevation;
    }

    public void setMinLst(double minLst) {
        this.minLst = minLst;
    }

    public double getMinLst() {
        return minLst;
    }

    public void setMaxLst(double maxLst) {
        this.maxLst = maxLst;
    }

    public double getMaxLst() {
        return maxLst;
    }

    public Projection getProjection() {
        return projection;
    }

    public void setModisTiles(ModisTile[] modisTiles) {
        this.modisTiles = modisTiles;
    }

    public ModisTile[] getModisTiles() {
        return modisTiles.clone(); // Defensive copying
    }

    public void setZonalSummaries(ZonalSummary[] zonalSummaries) {
        this.zonalSummaries = zonalSummaries;
    }

    public ZonalSummary[] getZonalSummaries() {
        return zonalSummaries.clone(); // Defensive copying
    }

    public List<String> getShapeFiles() {
        List<String> shapeFiles = new ArrayList<String>();
        for (ZonalSummary summary : zonalSummaries) {
            if (shapeFiles.indexOf(summary.getShapeFile()) == -1) {
                shapeFiles.add(summary.getShapeFile());
            }
        }
        return shapeFiles;
    }

    public void setShouldCalculateETa(boolean calculateETa) {
        this.calculateETa = calculateETa;
    }

    public boolean shouldCalculateETa() {
        return calculateETa;
    }

    @Override
    public String toString() {
        return new StringBuilder()
        .append("{name: ").append(name)
        .append(", start date: ").append(startDate.toString())
        .append(", watermask: ").append(watermask)
        .append(", elevation: ").append(elevation)
        .append(", projection: ").append(projection.toString())
        .append(", modisTiles: ")
        .append(", zonalSummaries: ")
        .append(", calculateETa: ").append(calculateETa)
        .append("}").toString();
    }

}
