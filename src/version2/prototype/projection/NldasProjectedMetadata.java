package version2.prototype.projection;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



import version2.prototype.projection.NldasDownloadMetadataHolder;
//import version2.prototype.download.NldasDownloadMetadata;
import version2.prototype.util.XmlUtils;

public class NldasProjectedMetadata  implements Comparable<NldasProjectedMetadata>{

    private static final String ROOT_ELEMENT_NAME = "NldasReprojectedMetadata";
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";
    private final NldasDownloadMetadataHolder mDownload;
    private final long mTimestamp;

    public NldasProjectedMetadata(NldasDownloadMetadataHolder download, long timestamp) {
        mDownload = download;
        mTimestamp = timestamp;
    }

    public NldasDownloadMetadataHolder getDownload() {
        return mDownload;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NldasProjectedMetadata ? equals((NldasProjectedMetadata)obj) : false;
    }

    public boolean equals(NldasProjectedMetadata o) {
        return mDownload.equals(o.mDownload) && mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(NldasProjectedMetadata o) {
        return mDownload.equals(o.mDownload);
    }

    @Override
    public int compareTo(NldasProjectedMetadata o) {
        return mDownload.compareTo(o.mDownload) != 0 ?
                mDownload.compareTo(o.mDownload):
                    Long.valueOf(mTimestamp).compareTo(Long.valueOf(o.mTimestamp));
    }

    @Override
    public int hashCode() {
        return  mDownload.hashCode() * 17 + Long.valueOf(mTimestamp).hashCode();
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);

        rootElement.appendChild(mDownload.toXml(doc));
        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));

        return rootElement;
    }

    public static NldasProjectedMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final NldasDownloadMetadataHolder download = NldasDownloadMetadataHolder.fromXml(XmlUtils.getChildElement(rootElement));
        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new NldasProjectedMetadata(download, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);

        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static NldasProjectedMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}