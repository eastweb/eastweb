package edu.sdstate.eastweb.prototype.reprojection;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.util.CollectionUtils;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public final class ModisReprojectedMetadata implements Comparable<ModisReprojectedMetadata> {
    private static final String ROOT_ELEMENT_NAME = "ModisReprojectedMetadata";
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";

    private final List<ModisDownloadMetadata> mDownloads;
    private final long mTimestamp;

    public ModisReprojectedMetadata(List<ModisDownloadMetadata> downloads, long timestamp) {
        final List<ModisDownloadMetadata> sortedDownloads =
            new ArrayList<ModisDownloadMetadata>(downloads);
        Collections.sort(sortedDownloads);
        mDownloads = Collections.unmodifiableList(sortedDownloads);

        mTimestamp = timestamp;
    }

    public List<ModisDownloadMetadata> getDownloads() {
        return mDownloads;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModisReprojectedMetadata) {
            return equals((ModisReprojectedMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(ModisReprojectedMetadata o) {
        return mDownloads.equals(o.mDownloads) &&
        mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(ModisReprojectedMetadata o) {
        return mDownloads.equals(o.mDownloads);
    }

    @Override
    public int compareTo(ModisReprojectedMetadata o) {
        int cmp = CollectionUtils.compareLists(mDownloads, o.mDownloads);
        if (cmp != 0) {
            return cmp;
        }

        return Long.valueOf(mTimestamp).compareTo(Long.valueOf(mTimestamp));
    }

    @Override
    public int hashCode() {
        int hash = mDownloads.hashCode();
        hash = hash * 17 + Long.valueOf(mTimestamp).hashCode();
        return hash;
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);

        for (ModisDownloadMetadata download : mDownloads) {
            final Element downloadElement = download.toXml(doc);
            rootElement.appendChild(downloadElement);
        }

        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));

        return rootElement;
    }

    public static ModisReprojectedMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final List<ModisDownloadMetadata> downloads = new ArrayList<ModisDownloadMetadata>();
        for (Element element : XmlUtils.getChildElements(rootElement)) {
            downloads.add(ModisDownloadMetadata.fromXml(element));
        }

        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new ModisReprojectedMetadata(downloads, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static ModisReprojectedMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}