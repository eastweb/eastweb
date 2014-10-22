package edu.sdstate.eastweb.prototype.download;
import java.io.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public final class ModisDownloadMetadata implements Comparable<ModisDownloadMetadata> {
    private static final String ROOT_ELEMENT_NAME = "ModisDownloadMetadata";
    private static final String DOWNLOADED_ATTRIBUTE_NAME = "downloaded";

    private final ModisId mModisId;
    private final DataDate mDownloaded;

    public ModisDownloadMetadata(ModisId modisId, DataDate downloaded) {
        mModisId = modisId;
        mDownloaded = downloaded;
    }

    public ModisId getModisId() {
        return mModisId;
    }

    public DataDate getDownloaded() {
        return mDownloaded;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModisDownloadMetadata) {
            return equals((ModisDownloadMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(ModisDownloadMetadata o) {
        return mModisId.equals(o.mModisId) &&
        mDownloaded.equals(o.mDownloaded);
    }

    public boolean equalsIgnoreDownloaded(ModisDownloadMetadata o) {
        return mModisId.equals(o.mModisId);
    }

    @Override
    public int hashCode() {
        int hash = mModisId.hashCode();
        hash = 17 * hash + mDownloaded.hashCode();
        return hash;
    }

    @Override
    public int compareTo(ModisDownloadMetadata o) {
        int cmp = mModisId.compareTo(o.mModisId);
        if (cmp != 0) {
            return cmp;
        }

        return mDownloaded.compareTo(mDownloaded);
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);
        rootElement.appendChild(mModisId.toXml(doc));
        rootElement.setAttribute(DOWNLOADED_ATTRIBUTE_NAME, mDownloaded.toCompactString());
        return rootElement;
    }

    public static ModisDownloadMetadata fromXml(Element rootElement) throws IOException {
        if (rootElement.getNodeName() != ROOT_ELEMENT_NAME) {
            throw new IOException("Unexpected root element name");
        }

        final ModisId modisId = ModisId.fromXml(XmlUtils.getChildElement(rootElement));
        final DataDate downloaded = DataDate.fromCompactString(rootElement.getAttribute(DOWNLOADED_ATTRIBUTE_NAME));

        return new ModisDownloadMetadata(modisId, downloaded);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static ModisDownloadMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}