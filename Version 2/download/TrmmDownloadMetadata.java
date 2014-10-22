package edu.sdstate.eastweb.prototype.download;

import java.io.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.util.XmlUtils;


public class TrmmDownloadMetadata implements Comparable<TrmmDownloadMetadata> {
    private static final String ROOT_ELEMENT_NAME = "TrmmDownloadMetadata";
    private static final String DATE_ATTRIBUTE_NAME = "date";
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";

    private final DataDate mDate;
    private final long mTimestamp;

    public TrmmDownloadMetadata(DataDate date, long timestamp) {
        mDate = date;
        mTimestamp = timestamp;
    }

    public DataDate getDate() {
        return mDate;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrmmDownloadMetadata) {
            return equals((TrmmDownloadMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(TrmmDownloadMetadata o) {
        return mDate.equals(o.mDate) &&
        mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(TrmmDownloadMetadata o) {
        return mDate.equals(o.mDate);
    }

    @Override
    public int compareTo(TrmmDownloadMetadata o) {
        int cmp = mDate.compareTo(o.mDate);
        if (cmp != 0) {
            return cmp;
        }

        return Long.valueOf(mTimestamp).compareTo(Long.valueOf(o.mTimestamp));
    }

    @Override
    public int hashCode() {
        int hash = mDate.hashCode();
        hash = hash * 17 + Long.valueOf(mTimestamp).hashCode();
        return hash;
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.setAttribute(DATE_ATTRIBUTE_NAME, mDate.toCompactString());
        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));
        return rootElement;
    }

    public static TrmmDownloadMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final DataDate date = DataDate.fromCompactString(
                rootElement.getAttribute(DATE_ATTRIBUTE_NAME));
        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new TrmmDownloadMetadata(date, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static TrmmDownloadMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}