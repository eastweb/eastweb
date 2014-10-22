package edu.sdstate.eastweb.prototype.download;


import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public final class EtoArchiveMetadata {
    private static final String ROOT_ELEMENT_NAME = "EtoArchiveMetadata";
    private static final String ARCHIVE_ELEMENT_NAME = "EtoArchiveMetadata.archive";
    private static final String DATE_ELEMENT_NAME = "Date";
    private static final String VALUE_ATTRIBUTE_NAME = "value";
    private static final String DOWNLOADED_ATTRIBUTE_NAME = "downloaded";

    private final EtoArchive mArchive;
    private final List<DataDate> mDates;
    private final DataDate mDownloaded;

    public EtoArchiveMetadata(EtoArchive archive, List<DataDate> dates, DataDate downloaded) {
        mArchive = archive;

        final List<DataDate> sortedDates = new ArrayList<DataDate>(dates);
        Collections.sort(sortedDates);
        mDates = Collections.unmodifiableList(sortedDates);

        mDownloaded = downloaded;
    }

    public EtoArchive getArchive() {
        return mArchive;
    }

    public List<DataDate> getDates() {
        return mDates;
    }

    public DataDate getDownloaded() {
        return mDownloaded;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EtoArchiveMetadata) {
            return equals((EtoArchiveMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(EtoArchiveMetadata o) {
        return mArchive.equals(o.mArchive) &&
        mDates.equals(o.mDates) &&
        mDownloaded.equals(o.mDownloaded);
    }

    public boolean equalsIgnoreDownloaded(EtoArchiveMetadata o) {
        return mArchive.equals(o.mArchive) &&
        mDates.equals(o.mDates);
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);

        final Element archiveElement = doc.createElement(ARCHIVE_ELEMENT_NAME);
        archiveElement.appendChild(mArchive.toXml(doc));
        rootElement.appendChild(archiveElement);

        for (DataDate date : mDates) {
            final Element dateElement = doc.createElement(DATE_ELEMENT_NAME);
            dateElement.setAttribute(VALUE_ATTRIBUTE_NAME, date.toCompactString());
            rootElement.appendChild(dateElement);
        }

        rootElement.setAttribute(DOWNLOADED_ATTRIBUTE_NAME, mDownloaded.toCompactString());

        return rootElement;
    }

    public static EtoArchiveMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final EtoArchive archive = EtoArchive.fromXml(
                XmlUtils.getChildElement(
                        XmlUtils.getElementByTagName(rootElement, ARCHIVE_ELEMENT_NAME)
                )
        );

        final List<DataDate> dates = new ArrayList<DataDate>();
        final NodeList dateElements = rootElement.getElementsByTagName(DATE_ELEMENT_NAME);
        for (int i = 0; i < dateElements.getLength(); ++i) {
            final Element dateElement = (Element)dateElements.item(i);
            dates.add(DataDate.fromCompactString(dateElement.getAttribute(VALUE_ATTRIBUTE_NAME)));
        }

        final DataDate downloaded = DataDate.fromCompactString(
                rootElement.getAttribute(DOWNLOADED_ATTRIBUTE_NAME));

        return new EtoArchiveMetadata(archive, dates, downloaded);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static EtoArchiveMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}