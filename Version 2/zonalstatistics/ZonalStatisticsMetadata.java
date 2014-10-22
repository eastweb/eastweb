package edu.sdstate.eastweb.prototype.zonalstatistics;

import java.io.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.indices.IndexMetadata;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public class ZonalStatisticsMetadata {
    private static final String ROOT_ELEMENT_NAME = "ZonalStatisticsMetadata";
    private static final String ZONAL_SUMMARY_ATTRIBUTE_NAME = "zonalSummary";
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";

    private final IndexMetadata mIndex;
    private final String mZonalSummary;
    private final long mTimestamp;

    public ZonalStatisticsMetadata(IndexMetadata index, String zonalSummary, long timestamp) {
        mIndex = index;
        mZonalSummary = zonalSummary;
        mTimestamp = timestamp;
    }

    public IndexMetadata getIndex() {
        return mIndex;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZonalStatisticsMetadata) {
            return equals((ZonalStatisticsMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(ZonalStatisticsMetadata o) {
        return mIndex.equals(o.mIndex) &&
        mZonalSummary.equals(o.mZonalSummary) &&
        mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(ZonalStatisticsMetadata o) {
        return mIndex.equals(o.mIndex) &&
        mZonalSummary.equals(o.mZonalSummary);
    }

    @Override
    public int hashCode() {
        int hash = mIndex.hashCode();
        hash = hash * 17 + Long.valueOf(mTimestamp).hashCode();
        return hash;
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.appendChild(mIndex.toXml(doc));
        rootElement.setAttribute(ZONAL_SUMMARY_ATTRIBUTE_NAME, mZonalSummary);
        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));
        return rootElement;
    }

    public static ZonalStatisticsMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final IndexMetadata index = IndexMetadata.fromXml(XmlUtils.getChildElement(rootElement));
        final String zonalSummary = rootElement.getAttribute(ZONAL_SUMMARY_ATTRIBUTE_NAME);
        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new ZonalStatisticsMetadata(index, zonalSummary, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static ZonalStatisticsMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}
