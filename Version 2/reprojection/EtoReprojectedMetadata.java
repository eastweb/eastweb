package edu.sdstate.eastweb.prototype.reprojection;

import java.io.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.download.EtoDownloadMetadata;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public final class EtoReprojectedMetadata implements Comparable<EtoReprojectedMetadata> {
    private static final String ROOT_ELEMENT_NAME = "EtoReprojectedMetadata";
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";

    private final EtoDownloadMetadata mDownload;
    private final long mTimestamp;

    public EtoReprojectedMetadata(EtoDownloadMetadata download, long timestamp) {
        mDownload = download;
        mTimestamp = timestamp;
    }

    public EtoDownloadMetadata getDownload() {
        return mDownload;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EtoReprojectedMetadata) {
            return equals((EtoReprojectedMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(EtoReprojectedMetadata o) {
        return mDownload.equals(o.mDownload) &&
        mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(EtoReprojectedMetadata o) {
        return mDownload.equals(o.mDownload);
    }

    @Override
    public int compareTo(EtoReprojectedMetadata o) {
        int cmp = mDownload.compareTo(o.mDownload);
        if (cmp != 0) {
            return cmp;
        }

        return Long.valueOf(mTimestamp).compareTo(Long.valueOf(o.mTimestamp));
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.appendChild(mDownload.toXml(doc));
        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));
        return rootElement;
    }

    public static EtoReprojectedMetadata fromXml(Element rootElement) throws IOException {
        if (rootElement.getNodeName() != ROOT_ELEMENT_NAME) {
            throw new IOException("Unexpected root element name");
        }

        final EtoDownloadMetadata date = EtoDownloadMetadata.fromXml(
                XmlUtils.getChildElement(rootElement));
        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new EtoReprojectedMetadata(date, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static EtoReprojectedMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}