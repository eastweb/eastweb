package edu.sdstate.eastweb.prototype.download.cache;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

/**
 * Represents a cached map from MODIS tiles to their processing dates.
 * Instances of this class are immutable.
 * 
 * @author Michael VanBemmel
 */
public class ModisTileCache implements Cache {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String ROOT_ELEMENT_NAME = "ModisTileCache";
    private static final String LAST_UPDATED_ATTRIBUTE_NAME = "lastUpdated";
    private static final String HORIZONTAL_GROUP_ELEMENT_NAME = "HGroup";
    private static final String TILE_ELEMENT_NAME = "Tile";
    private static final String HORIZONTAL_ATTRIBUTE_NAME = "h";
    private static final String VERTICAL_ATTRIBUTE_NAME = "v";
    private static final String PROCESSED_ATTRIBUTE_NAME = "processed";

    private final DataDate mLastUpdated;
    private final Map<ModisTile, DataDate> mTiles;

    public ModisTileCache(DataDate lastUpdated, Map<ModisTile, DataDate> tiles) {
        mLastUpdated = lastUpdated;
        mTiles = Collections.unmodifiableMap(new HashMap<ModisTile, DataDate>(tiles));
    }

    @Override
    public DataDate getLastUpdated() {
        return mLastUpdated;
    }

    public Map<ModisTile, DataDate> getTiles() {
        return mTiles;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModisTileCache) {
            return equals((ModisTileCache)obj);
        } else {
            return false;
        }
    }

    public boolean equals(ModisTileCache o) {
        return mLastUpdated.equals(o.mLastUpdated) &&
        mTiles.equals(o.mTiles);
    }

    public static ModisTileCache fromFile(File file) throws IOException {
        final Element rootElement = XmlUtils.parseGzipped(file).getDocumentElement();
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        // Get last updated date
        final DataDate lastUpdated = DataDate.fromCompactString(
                rootElement.getAttribute(LAST_UPDATED_ATTRIBUTE_NAME));

        // Read tiles
        final Map<ModisTile, DataDate> tiles = new HashMap<ModisTile, DataDate>();
        final NodeList hGroupNodes = rootElement.getElementsByTagName(
                HORIZONTAL_GROUP_ELEMENT_NAME);
        for (int i = 0; i < hGroupNodes.getLength(); ++i) {
            final Element hGroupElement = (Element)hGroupNodes.item(i);
            final int h = Integer.parseInt(hGroupElement.getAttribute(HORIZONTAL_ATTRIBUTE_NAME));

            final NodeList tileNodes = hGroupElement.getElementsByTagName(TILE_ELEMENT_NAME);
            for (int j = 0; j < tileNodes.getLength(); ++j) {
                final Element tileElement = (Element)tileNodes.item(j);
                final int v = Integer.parseInt(tileElement.getAttribute(VERTICAL_ATTRIBUTE_NAME));
                final DataDate processed = DataDate.fromCompactString(
                        tileElement.getAttribute(PROCESSED_ATTRIBUTE_NAME));
                tiles.put(new ModisTile(h, v), processed);
            }
        }

        return new ModisTileCache(lastUpdated, tiles);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);

        final Element rootElement = doc.getDocumentElement();
        rootElement.setAttribute(LAST_UPDATED_ATTRIBUTE_NAME, mLastUpdated.toCompactString());

        // Build a list of map entries and sort it for output
        final List<Entry<ModisTile, DataDate>> entries = new ArrayList<Entry<ModisTile,DataDate>>();
        entries.addAll(mTiles.entrySet());
        Collections.sort(entries, new Comparator<Entry<ModisTile, DataDate>>() {
            @Override
            public int compare(Entry<ModisTile, DataDate> o1, Entry<ModisTile, DataDate> o2) {
                int cmp = o1.getKey().getHTile() - o2.getKey().getHTile();
                if (cmp != 0) {
                    return cmp;
                }

                cmp = o1.getKey().getVTile() - o2.getKey().getVTile();
                return cmp;
            }
        });

        // Create an element for each tile
        int currentH = -1;
        Element hGroupElement = null;
        for (Entry<ModisTile, DataDate> entry : entries) {
            if (entry.getKey().getHTile() != currentH) {
                currentH = entry.getKey().getHTile();
                hGroupElement = doc.createElement(HORIZONTAL_GROUP_ELEMENT_NAME);
                hGroupElement.setAttribute(HORIZONTAL_ATTRIBUTE_NAME, Integer.toString(currentH));
                rootElement.appendChild(hGroupElement);
            }

            final Element tileElement = doc.createElement(TILE_ELEMENT_NAME);
            tileElement.setAttribute(VERTICAL_ATTRIBUTE_NAME,
                    Integer.toString(entry.getKey().getVTile()));
            tileElement.setAttribute(PROCESSED_ATTRIBUTE_NAME, entry.getValue().toCompactString());
            hGroupElement.appendChild(tileElement);
        }

        XmlUtils.transformToGzippedFile(doc, file);
    }
}