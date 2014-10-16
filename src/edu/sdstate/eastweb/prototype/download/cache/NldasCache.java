package edu.sdstate.eastweb.prototype.download.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.util.XmlUtils;

public class NldasCache implements Cache{

    private static final long serialVersionUID = 1L;
    private static final String ROOT_ELEMENT_NAME = "DateCache";
    private static final String LAST_UPDATED_ATTRIBUTE_NAME = "lastUpdated";
    private static final String START_DATE_ATTRIBUTE_NAME = "startDate";
    private static final String YEAR_ELEMENT_NAME = "Year";
    private static final String YEAR_ATTRIBUTE_NAME = "value";
    private static final String DAY_ELEMENT_NAME = "Day";
    private static final String DAY_ATTRIBUTE_NAME = "value";
    private static final String HOUR_ELEMENT_NAME="Hour";

    private final DataDate mLastUpdated;
    private final DataDate mStartDate;
    private final List<DataDate> mDates;

    public NldasCache(DataDate lastUpdated, DataDate startDate, List<DataDate> dates) {
        mLastUpdated = lastUpdated;
        mStartDate = startDate;

        final List<DataDate> listCopy = new ArrayList<DataDate>(dates);
        Collections.sort(listCopy);
        mDates = Collections.unmodifiableList(listCopy);
    }

    @Override
    public DataDate getLastUpdated() {
        return mLastUpdated;
    }
    public DataDate getStartDate() {
        return mStartDate;
    }

    public List<DataDate> getDates() {
        return mDates;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DateCache) {
            return equals(obj);
        } else {
            return false;
        }
    }

    public boolean equals(NldasCache o) {
        return mLastUpdated.equals(o.mLastUpdated) &&
        mStartDate.equals(o.mStartDate) &&
        mDates.equals(o.mDates);
    }

    public static NldasCache fromFile(File file) throws IOException {
        final Element rootElement = XmlUtils.parseGzipped(file).getDocumentElement();
        System.out.println(file.getPath()+" is open");
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        // Get last updated date
        final DataDate lastUpdated = DataDate.fromCompactString(
                rootElement.getAttribute(LAST_UPDATED_ATTRIBUTE_NAME));

        // Get start date
        final DataDate startDate = DataDate.fromCompactString(
                rootElement.getAttribute(START_DATE_ATTRIBUTE_NAME));

        // Read data dates
        final List<DataDate> dates = new ArrayList<DataDate>();
        final NodeList yearNodes = rootElement.getElementsByTagName(YEAR_ELEMENT_NAME);
        for (int i = 0; i < yearNodes.getLength(); ++i) {
            final Element yearElement = (Element)yearNodes.item(i);
            final int year = Integer.parseInt(yearElement.getAttribute(YEAR_ATTRIBUTE_NAME));

            final NodeList dayNodes = yearElement.getElementsByTagName(DAY_ELEMENT_NAME);
            for (int j = 0; j < dayNodes.getLength(); ++j) {
                final Element dayElement = (Element)dayNodes.item(j);
                final int day = Integer.parseInt(dayElement.getAttribute(DAY_ATTRIBUTE_NAME));
                final NodeList hourNodes=dayElement.getElementsByTagName(HOUR_ELEMENT_NAME);
                for(int k=0; k<hourNodes.getLength(); ++k){
                    final Element hourElement=(Element)hourNodes.item(k);
                    final int hour=Integer.parseInt(hourElement.getTextContent());
                    dates.add(DataDate.DataDateWithHour(hour, day, year));
                }
            }
        }

        return new NldasCache(lastUpdated, startDate, dates);
    }

    public void toFile(File file) throws IOException {
        final Document doc = XmlUtils.newDocument(ROOT_ELEMENT_NAME);

        final Element rootElement = doc.getDocumentElement();
        rootElement.setAttribute(LAST_UPDATED_ATTRIBUTE_NAME, mLastUpdated.toCompactString());
        rootElement.setAttribute(START_DATE_ATTRIBUTE_NAME, mStartDate.toCompactString());

        int currentYear = -1;
        Element yearElement = null;
        for (DataDate date : mDates) {
            // Create a year element for each new year
            if (date.getYear() != currentYear) {
                currentYear = date.getYear();
                yearElement = doc.createElement(YEAR_ELEMENT_NAME);
                yearElement.setAttribute(YEAR_ATTRIBUTE_NAME, Integer.toString(currentYear));
                rootElement.appendChild(yearElement);
            }

            final Element dayElement = doc.createElement(DAY_ELEMENT_NAME);
            //dayElement.setTextContent(Integer.toString(date.getDayOfYear()));
            dayElement.setAttribute(DAY_ATTRIBUTE_NAME, Integer.toString(date.getDayOfYear()));
            final Element hourElement=doc.createElement(HOUR_ELEMENT_NAME);
            hourElement.setTextContent(Integer.toString(date.getHour()));
            dayElement.appendChild(hourElement);
            yearElement.appendChild(dayElement);
        }

        XmlUtils.transformToGzippedFile(doc, file);
    }
}
