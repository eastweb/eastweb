package edu.sdstate.eastweb.prototype.reprojection;

import java.io.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.download.TrmmDownloadMetadata;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public class TrmmReprojectedMetadata implements Comparable<TrmmReprojectedMetadata> {
    private static final String ROOT_ELEMENT_NAME = "TrmmReprojectedMetadata";
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";

    private final TrmmDownloadMetadata mDownload;
    private final long mTimestamp;

    public TrmmReprojectedMetadata(TrmmDownloadMetadata download, long timestamp) {
        mDownload = download;
        mTimestamp = timestamp;
    }

    public TrmmDownloadMetadata getDownload() {
        return mDownload;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrmmReprojectedMetadata) {
            return equals((TrmmReprojectedMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(TrmmReprojectedMetadata o) {
        return mDownload.equals(o.mDownload) &&
        mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(TrmmReprojectedMetadata o) {
        return mDownload.equals(o.mDownload);
    }

    @Override
    public int compareTo(TrmmReprojectedMetadata o) {
        int cmp = mDownload.compareTo(o.mDownload);
        if (cmp != 0) {
            return cmp;
        }

        return Long.valueOf(mTimestamp).compareTo(Long.valueOf(o.mTimestamp));
    }

    @Override
    public int hashCode() {
        int hash = mDownload.hashCode();
        hash = hash * 17 + Long.valueOf(mTimestamp).hashCode();
        return hash;
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.appendChild(mDownload.toXml(doc));
        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));
        return rootElement;
    }

    public static TrmmReprojectedMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final TrmmDownloadMetadata download = TrmmDownloadMetadata.fromXml(
                XmlUtils.getChildElement(rootElement));
        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new TrmmReprojectedMetadata(download, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static TrmmReprojectedMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}