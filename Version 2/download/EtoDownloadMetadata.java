package edu.sdstate.eastweb.prototype.download;


import java.io.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public final class EtoDownloadMetadata implements Comparable<EtoDownloadMetadata> {
    private static final String ROOT_ELEMENT_NAME = "EtoDownloadMetadata";
    private static final String DATE_ATTRIBUTE_NAME = "date";
    private static final String DOWNLOADED_ATTRIBUTE_NAME = "downloaded";

    private final DataDate mDate;
    private final DataDate mDownloaded;

    public EtoDownloadMetadata(DataDate date, DataDate downloaded) {
        mDate = date;
        mDownloaded = downloaded;
    }

    public DataDate getDate() {
        return mDate;
    }

    public DataDate getDownloaded() {
        return mDownloaded;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EtoDownloadMetadata) {
            return equals((EtoDownloadMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(EtoDownloadMetadata o) {
        return mDate.equals(o.mDate) &&
        mDownloaded.equals(o.mDownloaded);
    }

    @Override
    public int compareTo(EtoDownloadMetadata o) {
        int cmp = mDate.compareTo(o.mDate);
        if (cmp != 0) {
            return cmp;
        }

        return mDownloaded.compareTo(o.mDownloaded);
    }

    @Override
    public int hashCode() {
        int hash = mDate.hashCode();
        hash = hash * 17 + mDownloaded.hashCode();
        return hash;
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.setAttribute(DATE_ATTRIBUTE_NAME, mDate.toCompactString());
        rootElement.setAttribute(DOWNLOADED_ATTRIBUTE_NAME, mDownloaded.toCompactString());
        return rootElement;
    }

    public static EtoDownloadMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final DataDate date = DataDate.fromCompactString(
                rootElement.getAttribute(DATE_ATTRIBUTE_NAME));
        final DataDate downloaded = DataDate.fromCompactString(
                rootElement.getAttribute(DOWNLOADED_ATTRIBUTE_NAME));

        return new EtoDownloadMetadata(date, downloaded);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static EtoDownloadMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}