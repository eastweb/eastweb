package edu.sdstate.eastweb.prototype.indices;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.reprojection.*;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public class IndexMetadata {
    private static final String ROOT_ELEMENT_NAME = "IndexMetadata";
    private static final String MODIS_ELEMENT_NAME = "IndexMetadata.modis";
    private static final String TRMM_ELEMENT_NAME = "IndexMetadata.trmm";
    private static final String ETO_ELEMENT_NAME = "IndexMetadata.eto";
    private static final String SHAPE_FILE_ATTRIBUTE_NAME = "zonalSummaryId"; // FIXME: change to shape file!
    private static final String TIMESTAMP_ATTRIBUTE_NAME = "timestamp";

    private final List<ModisReprojectedMetadata> mModis;
    private final List<TrmmReprojectedMetadata> mTrmm;
    private final List<EtoReprojectedMetadata> mEto;
    private final String mShapeFile;
    private final long mTimestamp;

    public IndexMetadata(List<ModisReprojectedMetadata> modis, List<TrmmReprojectedMetadata> trmm,
            List<EtoReprojectedMetadata> eto, String shapeFile, long timestamp) {
        final List<ModisReprojectedMetadata> modisSorted =
            new ArrayList<ModisReprojectedMetadata>(modis);
        Collections.sort(modisSorted);
        mModis = Collections.unmodifiableList(modisSorted);

        final List<TrmmReprojectedMetadata> trmmSorted =
            new ArrayList<TrmmReprojectedMetadata>(trmm);
        Collections.sort(trmmSorted);
        mTrmm = Collections.unmodifiableList(trmmSorted);

        final List<EtoReprojectedMetadata> etoSorted = new ArrayList<EtoReprojectedMetadata>(eto);
        Collections.sort(etoSorted);
        mEto = Collections.unmodifiableList(etoSorted);

        mShapeFile = shapeFile;

        mTimestamp = timestamp;
    }

    public List<ModisReprojectedMetadata> getModis() {
        return mModis;
    }

    public List<TrmmReprojectedMetadata> getTrmm() {
        return mTrmm;
    }

    public List<EtoReprojectedMetadata> getEto() {
        return mEto;
    }

    public String getZonalSummaryId() {
        return mShapeFile;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexMetadata) {
            return equals((IndexMetadata)obj);
        } else {
            return false;
        }
    }

    public boolean equals(IndexMetadata o) {
        return mModis.equals(o.mModis) &&
        mTrmm.equals(o.mTrmm) &&
        mEto.equals(o.mEto) &&
        mShapeFile.equals(o.mShapeFile)&&
        mTimestamp == o.mTimestamp;
    }

    public boolean equalsIgnoreTimestamp(IndexMetadata o) {
        return mModis.equals(o.mModis) &&
        mTrmm.equals(o.mTrmm) &&
        mEto.equals(o.mEto) &&
        mShapeFile.equals(o.mShapeFile);
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);

        final Element modisElement = doc.createElement(MODIS_ELEMENT_NAME);
        for (ModisReprojectedMetadata modis : mModis) {
            modisElement.appendChild(modis.toXml(doc));
        }
        rootElement.appendChild(modisElement);

        final Element trmmElement = doc.createElement(TRMM_ELEMENT_NAME);
        for (TrmmReprojectedMetadata trmm : mTrmm) {
            trmmElement.appendChild(trmm.toXml(doc));
        }
        rootElement.appendChild(trmmElement);

        final Element etoElement = doc.createElement(ETO_ELEMENT_NAME);
        for (EtoReprojectedMetadata eto : mEto) {
            etoElement.appendChild(eto.toXml(doc));
        }
        rootElement.appendChild(etoElement);

        rootElement.setAttribute(SHAPE_FILE_ATTRIBUTE_NAME, mShapeFile);
        rootElement.setAttribute(TIMESTAMP_ATTRIBUTE_NAME, Long.toString(mTimestamp));

        return rootElement;
    }

    public static IndexMetadata fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final List<ModisReprojectedMetadata> modis = new ArrayList<ModisReprojectedMetadata>();
        final Element modisElement = XmlUtils.getElementByTagName(rootElement, MODIS_ELEMENT_NAME);
        for (Element element : XmlUtils.getChildElements(modisElement)) {
            modis.add(ModisReprojectedMetadata.fromXml(element));
        }

        final List<TrmmReprojectedMetadata> trmm = new ArrayList<TrmmReprojectedMetadata>();
        final Element trmmElement = XmlUtils.getElementByTagName(rootElement, TRMM_ELEMENT_NAME);
        for (Element element : XmlUtils.getChildElements(trmmElement)) {
            trmm.add(TrmmReprojectedMetadata.fromXml(element));
        }

        final List<EtoReprojectedMetadata> eto = new ArrayList<EtoReprojectedMetadata>();
        final Element etoElement = XmlUtils.getElementByTagName(rootElement, ETO_ELEMENT_NAME);
        for (Element element : XmlUtils.getChildElements(etoElement)) {
            eto.add(EtoReprojectedMetadata.fromXml(element));
        }

        final String shapeFile = rootElement.getAttribute(SHAPE_FILE_ATTRIBUTE_NAME);
        final long timestamp = Long.parseLong(rootElement.getAttribute(TIMESTAMP_ATTRIBUTE_NAME));

        return new IndexMetadata(modis, trmm, eto, shapeFile, timestamp);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);
        doc.replaceChild(toXml(doc), doc.getDocumentElement());
        XmlUtils.transformToGzippedFile(doc, file);
    }

    public static IndexMetadata fromFile(File file) throws IOException {
        return fromXml(XmlUtils.parseGzipped(file).getDocumentElement());
    }
}