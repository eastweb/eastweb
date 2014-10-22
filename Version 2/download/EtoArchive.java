package edu.sdstate.eastweb.prototype.download;

import java.io.IOException;
import java.io.Serializable;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.DataDate;

/**
 * Represents a unique identifier for a FEWS NET ETo archive.
 * 
 * @author Michael VanBemmel
 */
public final class EtoArchive implements Comparable<EtoArchive>, Serializable {

    private static final long serialVersionUID = 1L;

    public enum Type {
        Yearly(false, false),
        Monthly(true, false),
        Daily(true, true);

        private final boolean mHasMonth;
        private final boolean mHasDay;

        private Type(boolean hasMonth, boolean hasDay) {
            mHasMonth = hasMonth;
            mHasDay = hasDay;
        }

        public boolean getHasMonth() {
            return mHasMonth;
        }

        public boolean getHasDay() {
            return mHasDay;
        }
    }

    private static final String ROOT_ELEMENT_NAME = "EtoArchive";
    private static final String TYPE_ATTRIBUTE_NAME = "type";
    private static final String YEAR_ATTRIBUTE_NAME = "year";
    private static final String MONTH_ATTRIBUTE_NAME = "month";
    private static final String DAY_ATTRIBUTE_NAME = "day";

    private final Type mType;
    private final int mYear;
    private final int mMonth;
    private final int mDay;

    private EtoArchive(Type type, int year, int month, int day) {
        mType = type;
        mYear = year;
        mMonth = month;
        mDay = day;
    }

    public static EtoArchive yearly(int year) {
        return new EtoArchive(Type.Yearly, year, 0, 0);
    }

    public static EtoArchive monthly(int year, int month) {
        return new EtoArchive(Type.Monthly, year, month, 0);
    }

    public static EtoArchive daily(int year, int month, int day) {
        return new EtoArchive(Type.Daily, year, month, day);
    }

    public Type getType() {
        return mType;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        if (!mType.getHasMonth()) {
            throw new IllegalStateException();
        }
        return mMonth;
    }

    public int getDay() {
        if (!mType.getHasDay()) {
            throw new IllegalStateException();
        }
        return mDay;
    }

    public DataDate toDataDate() {
        if (mType != Type.Daily) {
            throw new IllegalStateException();
        }
        return new DataDate(mDay, mMonth, mYear);
    }

    @Override
    public int compareTo(EtoArchive archive) {
        int cmp = mType.compareTo(archive.mType);
        if (cmp != 0) {
            return cmp;
        }

        if (mYear < archive.mYear) {
            return -1;
        } else if (mYear > archive.mYear) {
            return 1;
        }

        if (mMonth < archive.mMonth) {
            return -1;
        } else if (mMonth > archive.mMonth) {
            return 1;
        }

        if (mDay < archive.mDay) {
            return -1;
        } else if (mDay > archive.mDay) {
            return 1;
        }

        // The objects are equal
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EtoArchive) {
            return equals((EtoArchive)obj);
        } else {
            return false;
        }
    }

    public boolean equals(EtoArchive archive) {
        return mType == archive.mType &&
        mYear == archive.mYear &&
        mMonth == archive.mMonth &&
        mDay == archive.mDay;
    }

    @Override
    public int hashCode() {
        int hashCode = mType.hashCode();
        hashCode = hashCode * 17 + mYear;
        hashCode = hashCode * 17 + mMonth;
        hashCode = hashCode * 17 + mDay;
        return hashCode;
    }

    @Override
    public String toString() {
        switch (mType) {
        case Yearly:
            return String.format("{type: Yearly, year: %04d}", mYear);

        case Monthly:
            return String.format("{type: Monthly, year: %04d, month: %02d}", mYear, mMonth);

        case Daily:
            return String.format("{type: Daily, year: %04d, month: %02d, day: %02d}", mYear, mMonth, mDay);

        default:
            return "{type: (invalid)}";
        }
    }

    public Element toXml(Document doc) {
        final Element rootElement = doc.createElement(ROOT_ELEMENT_NAME);

        rootElement.setAttribute(TYPE_ATTRIBUTE_NAME, mType.toString());
        rootElement.setAttribute(YEAR_ATTRIBUTE_NAME, Integer.toString(mYear));
        if (mType.getHasMonth()) {
            rootElement.setAttribute(MONTH_ATTRIBUTE_NAME, Integer.toString(mMonth));
        }
        if (mType.getHasDay()) {
            rootElement.setAttribute(DAY_ATTRIBUTE_NAME, Integer.toString(mDay));
        }

        return rootElement;
    }

    public static EtoArchive fromXml(Element rootElement) throws IOException {
        if (!rootElement.getNodeName().equals(ROOT_ELEMENT_NAME)) {
            throw new IOException("Unexpected root element name");
        }

        final Type type = Type.valueOf(rootElement.getAttribute(TYPE_ATTRIBUTE_NAME));
        final int year = Integer.parseInt(rootElement.getAttribute(YEAR_ATTRIBUTE_NAME));
        final int month = type.getHasMonth() ? Integer.parseInt(rootElement.getAttribute(MONTH_ATTRIBUTE_NAME)) : 0;
        final int day = type.getHasDay() ? Integer.parseInt(rootElement.getAttribute(DAY_ATTRIBUTE_NAME)) : 0;

        return new EtoArchive(type, year, month, day);
    }
}