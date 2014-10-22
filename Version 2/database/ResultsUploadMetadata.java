package edu.sdstate.eastweb.prototype.database;

import java.io.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import edu.sdstate.eastweb.prototype.zonalstatistics.ZonalStatisticsMetadata;

public final class ResultsUploadMetadata {
    private static final String ROOT_ELEMENT_NAME = "ResultsUploadMetadata";
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";

    private final ZonalStatisticsMetadata mZonalStatistics;
    private final long mTimestamp;

    public ResultsUploadMetadata(ZonalStatisticsMetadata zonalStatistics, long timestamp) {
        mZonalStatistics = zonalStatistics;
        mTimestamp = timestamp;
    }

    public ZonalStatisticsMetadata getZonalStatistics() {
        return mZonalStatistics;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResultsUploadMetadata) {
            return equals((ResultsUploadMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(ResultsUploadMetadata o) {
        return mZonalStatistics.equals(o.mZonalStatistics) &&
        mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(ResultsUploadMetadata o) {
        return mZonalStatistics.equals(o.mZonalStatistics);
    }

    @Override
    public int hashCode() {
        int hash = mZonalStatistics.hashCode();
        hash = hash * 17 + Long.valueOf(mTimestamp).hashCode();
        return hash;
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.appendChild(mZonalStatistics.toXml(doc));
        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));
        return rootElement;
    }

    public static ResultsUploadMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final ZonalStatisticsMetadata zonalStatistics = ZonalStatisticsMetadata.fromXml(
                XmlUtils.getChildElement(rootElement));
        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new ResultsUploadMetadata(zonalStatistics, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static ResultsUploadMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}