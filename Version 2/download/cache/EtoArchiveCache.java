package edu.sdstate.eastweb.prototype.download.cache;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.EtoArchive;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

/**
 * Represents a set of cached, sorted lists of ETa archives.
 * Instances of this class are immutable.
 * 
 * @author Michael VanBemmel
 */
public final class EtoArchiveCache implements Cache {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String ROOT_ELEMENT_NAME = "EtoArchiveCache2";
    private static final String LAST_UPDATED_ATTRIBUTE_NAME = "lastUpdated";
    private static final String START_DATE_ATTRIBUTE_NAME = "startDate";

    private final DataDate mLastUpdated;
    private final DataDate mStartDate;
    private final List<EtoArchive> mArchives;

    public EtoArchiveCache(DataDate lastUpdated, DataDate startDate, List<EtoArchive> archives) {
        mLastUpdated = lastUpdated;
        mStartDate = startDate;

        final List<EtoArchive> sortedArchives = new ArrayList<EtoArchive>(archives);
        Collections.sort(sortedArchives);
        mArchives = Collections.unmodifiableList(sortedArchives);
    }

    @Override
    public DataDate getLastUpdated() {
        return mLastUpdated;
    }

    public DataDate getStartDate() {
        return mStartDate;
    }

    public List<EtoArchive> getArchives() {
        return mArchives;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EtoArchiveCache) {
            return equals((EtoArchiveCache)obj);
        } else {
            return false;
        }
    }

    public boolean equals(EtoArchiveCache o) {
        return mLastUpdated.equals(o.mLastUpdated) &&
        mStartDate.equals(o.mStartDate) &&
        mArchives.equals(o.mArchives);
    }

    public static EtoArchiveCache fromFile(File file) throws IOException {
        final Element rootElement = XmlUtils.parseGzipped(file).getDocumentElement();
        if (rootElement.getNodeName() != ROOT_ELEMENT_NAME) {
            throw new IOException("Unexpected root element name");
        }

        // Get last updated date
        final DataDate lastUpdated = DataDate.fromCompactString(
                rootElement.getAttribute(LAST_UPDATED_ATTRIBUTE_NAME));

        // Get start date
        final DataDate startDate = DataDate.fromCompactString(
                rootElement.getAttribute(START_DATE_ATTRIBUTE_NAME));

        final List<EtoArchive> archives = new ArrayList<EtoArchive>();

        // Read archives
        for (Element element : XmlUtils.getChildElements(rootElement)) {
            archives.add(EtoArchive.fromXml(element));
        }

        return new EtoArchiveCache(lastUpdated, startDate, archives);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);

        final Element rootElement = doc.getDocumentElement();
        rootElement.setAttribute(LAST_UPDATED_ATTRIBUTE_NAME, mLastUpdated.toCompactString());
        rootElement.setAttribute(START_DATE_ATTRIBUTE_NAME, mStartDate.toCompactString());

        // Write archives
        for (EtoArchive archive : mArchives) {
            rootElement.appendChild(archive.toXml(doc));
        }

        XmlUtils.transformToGzippedFile(doc, file);
    }
}