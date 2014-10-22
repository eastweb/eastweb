package edu.sdstate.eastweb.prototype;

import java.io.IOException;
import java.io.Serializable;

import org.w3c.dom.*;

/**
 * Represents information about Modis tiles. Reference: http://modis-land.gsfc.nasa.gov/MODLAND_grid.htm.
 * 
 * @author Michael VanBemmel
 * @author Isaiah Snell-Feikema
 */
public final class ModisTile implements Comparable<ModisTile>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final int HORZ_MIN = 0;
    public static final int HORZ_MAX = 35;
    public static final int VERT_MIN = 0;
    public static final int VERT_MAX = 17;

    private static final String ROOT_ELEMENT_NAME = "ModisTile";
    private static final String HORIZONTAL_ATTRIBUTE_NAME = "h";
    private static final String VERTICAL_ATTRIBUTE_NAME = "v";

    private final int hTile;
    private final int vTile;

    public ModisTile(int hTile, int vTile) {
        if (hTile < HORZ_MIN || hTile > HORZ_MAX) {
            throw new IllegalArgumentException("Horizontal tile number out of range");
        }
        if (vTile < VERT_MIN || vTile > VERT_MAX) {
            throw new IllegalArgumentException("Vertical tile number out of range");
        }

        this.hTile = hTile;
        this.vTile = vTile;
    }

    public int getHTile() {
        return hTile;
    }

    public int getVTile() {
        return vTile;
    }

    @Override
    public int compareTo(ModisTile o) {
        // First, order by hTile
        if (hTile < o.hTile) {
            return -1;
        } else if (hTile > o.hTile) {
            return 1;
        }

        // Second, order by vTile
        if (vTile < o.vTile) {
            return -1;
        } else if (vTile > o.vTile) {
            return 1;
        }

        // The objects are equal
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModisTile) {
            return equals((ModisTile)obj);
        } else {
            return false;
        }
    }

    public boolean equals(ModisTile tile) {
        return hTile == tile.hTile && vTile == tile.vTile;
    }

    @Override
    public int hashCode() {
        int hash = hTile;
        hash = hash * 17 + vTile;
        return hash;
    }

    @Override
    public String toString() {
        return new StringBuilder()
        .append("{hTile: ").append(hTile)
        .append(", vTile: ").append(vTile)
        .append("}").toString();
    }

    public String toCompactString() {
        return String.format("h%02dv%02d", hTile, vTile);
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.setAttribute(HORIZONTAL_ATTRIBUTE_NAME, Integer.toString(hTile));
        rootElement.setAttribute(VERTICAL_ATTRIBUTE_NAME, Integer.toString(vTile));
        return rootElement;
    }

    public static ModisTile fromXml(Element rootElement) throws IOException {
        if (rootElement.getNodeName() != ROOT_ELEMENT_NAME) {
            throw new IOException("Unexpected root element name");
        }

        final int hTile = Integer.parseInt(rootElement.getAttribute(HORIZONTAL_ATTRIBUTE_NAME));
        final int vTile = Integer.parseInt(rootElement.getAttribute(VERTICAL_ATTRIBUTE_NAME));

        return new ModisTile(hTile, vTile);
    }
}