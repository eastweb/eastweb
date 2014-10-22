package edu.sdstate.eastweb.prototype;

import java.io.Serializable;

public class ZonalSummary implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String shapeFile;
    private final String field;

    public ZonalSummary(String name, String shapeFile, String field) {
        this.name = name;
        this.shapeFile = shapeFile;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public String getShapeFile() {
        return shapeFile;
    }

    public String getField() {
        return field;
    }

    @Override
    public String toString() {
        return String.format(
                "[name=%s, shapefile=%s, field=%s]",
                name, shapeFile, field
        );
    }

}
