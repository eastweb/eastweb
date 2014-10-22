package edu.sdstate.eastweb.prototype.download;


import java.io.IOException;
import java.io.Serializable;

import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public final class ModisId implements Comparable<ModisId>, Serializable {
    private static final long serialVersionUID = 1L;

    private static final String ROOT_ELEMENT_NAME = "ModisId";
    private static final String PRODUCT_ATTRIBUTE_NAME = "product";
    private static final String DATE_ATTRIBUTE_NAME = "date";
    private static final String PROCESSED_ATTRIBUTE_NAME = "processed";

    private final ModisProduct mProduct;
    private final DataDate mDate;
    private final ModisTile mTile;
    private final DataDate mProcessed;

    public ModisId(ModisProduct product, DataDate date, ModisTile tile, DataDate processed) {
        mProduct = product;
        mDate = date;
        mTile = tile;
        mProcessed = processed;
    }

    public ModisProduct getProduct() {
        return mProduct;
    }

    public DataDate getDate() {
        return mDate;
    }

    public ModisTile getTile() {
        return mTile;
    }

    public DataDate getProcessed() {
        return mProcessed;
    }

    @Override
    public int compareTo(ModisId o) {
        int cmp = mProduct.compareTo(o.mProduct);
        if (cmp != 0) {
            return cmp;
        }

        cmp = mDate.compareTo(o.mDate);
        if (cmp != 0) {
            return cmp;
        }

        cmp = mTile.compareTo(o.mTile);
        if (cmp != 0) {
            return cmp;
        }

        return mProcessed.compareTo(o.mProcessed);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModisId) {
            return equals((ModisId)obj);
        } else {
            return false;
        }
    }

    public boolean equals(ModisId value) {
        return mProduct == value.mProduct &&
        mDate.equals(value.mDate) &&
        mTile.equals(value.mTile) &&
        mProcessed.equals(value.mProcessed);
    }

    @Override
    public int hashCode() {
        int hash = mProduct.hashCode();
        hash = hash * 17 + mDate.hashCode();
        hash = hash * 17 + mTile.hashCode();
        hash = hash * 17 + mProcessed.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return new StringBuilder()
        .append("{ product: ").append(mProduct)
        .append(", date: ").append(mDate)
        .append(", tile: ").append(mTile)
        .append(", processed: ").append(mProcessed)
        .append(" }").toString();
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.setAttribute(PRODUCT_ATTRIBUTE_NAME, mProduct.toString());
        rootElement.setAttribute(DATE_ATTRIBUTE_NAME, mDate.toCompactString());
        rootElement.appendChild(mTile.toXml(doc));
        rootElement.setAttribute(PROCESSED_ATTRIBUTE_NAME, mProcessed.toCompactString());
        return rootElement;
    }

    public static ModisId fromXml(Element rootElement) throws IOException {
        if (rootElement.getNodeName() != ROOT_ELEMENT_NAME) {
            throw new IOException("Unexpected root element name");
        }

        final ModisProduct product = ModisProduct.valueOf(rootElement.getAttribute(PRODUCT_ATTRIBUTE_NAME));
        final DataDate date = DataDate.fromCompactString(rootElement.getAttribute(DATE_ATTRIBUTE_NAME));
        final ModisTile tile = ModisTile.fromXml(XmlUtils.getChildElement(rootElement));
        final DataDate processed = DataDate.fromCompactString(rootElement.getAttribute(PROCESSED_ATTRIBUTE_NAME));

        return new ModisId(product, date, tile, processed);
    }
}