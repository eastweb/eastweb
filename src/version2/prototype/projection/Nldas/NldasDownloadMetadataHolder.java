package version2.prototype.projection.Nldas;

import java.io.*;

import org.w3c.dom.*;

import version2.prototype.DataDate;
import version2.prototype.util.XmlUtils;

public class NldasDownloadMetadataHolder {
    private static final String ROOT_ELEMENT_NAME = "NldasDownloadMetadata";
    private static final String DATE_ATTRIBUTE_NAME = "date";
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";

    private final DataDate mDate;
    private final long mTimestamp;

    public NldasDownloadMetadataHolder(DataDate date, long timestamp) {
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
        return obj instanceof NldasDownloadMetadataHolder ? equals((NldasDownloadMetadataHolder)obj) : false;
    }

    public boolean equals(NldasDownloadMetadataHolder o) {
        return mDate.equals(o.mDate) && mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(NldasDownloadMetadataHolder o) {
        return mDate.equals(o.mDate);
    }

    public int compareTo(NldasDownloadMetadataHolder o) {
        return mDate.compareTo(o.mDate) != 0 ?
                mDate.compareTo(o.mDate):
                    Long.valueOf(mTimestamp).compareTo(Long.valueOf(o.mTimestamp));
    }

    @Override
    public int hashCode() {
        return mDate.hashCode() * 17 + Long.valueOf(mTimestamp).hashCode();
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);

        rootElement.setAttribute(DATE_ATTRIBUTE_NAME, mDate.toCompactString());
        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));

        return rootElement;
    }

    public static NldasDownloadMetadataHolder fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final DataDate date = DataDate.fromCompactString(rootElement.getAttribute(DATE_ATTRIBUTE_NAME));
        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new NldasDownloadMetadataHolder(date, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);

        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static NldasDownloadMetadataHolder fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}