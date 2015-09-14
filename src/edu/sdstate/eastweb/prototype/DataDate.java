package edu.sdstate.eastweb.prototype;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public final class DataDate implements Comparable<DataDate>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int day;
    private final int month;
    private final int year;
    private final int dayOfYear;
    //the range of hour is 0-23. default value is 0
    private final int hour;
    /**
     * Returns a GregorianCalendar initialized with today's date.
     */
    private static GregorianCalendar getTodayCalendar() {
        final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.ROOT);
        cal.setLenient(false);
        return cal;
    }

    /**
     * Returns a cleared GregorianCalendar.
     */
    private static GregorianCalendar getClearedCalendar() {
        final GregorianCalendar cal = getTodayCalendar();
        cal.clear();
        return cal;
    }

    /**
     * Returns a GregorianCalendar initialized with this instance's date.
     */
    private GregorianCalendar getCalendar() {
        final GregorianCalendar cal = getClearedCalendar();
        cal.set(year, month - 1, day); // Subtract 1 to convert from 1-based months to 0-based months
        return cal;
    }

    public DataDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        hour=0;
        // Compute dayOfYear
        GregorianCalendar cal = getClearedCalendar();
        cal.set(year, month - 1, day); // Subtract 1 to convert from 1-based months to 0-based months
        dayOfYear = cal.get(GregorianCalendar.DAY_OF_YEAR);
    }

    public DataDate(int dayOfYear, int year) {
        this.dayOfYear = dayOfYear;
        this.year = year;
        hour=0;

        // Compute day, month
        GregorianCalendar cal = getClearedCalendar();
        cal.set(GregorianCalendar.YEAR, year);
        cal.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);
        day = cal.get(GregorianCalendar.DAY_OF_MONTH);
        month = cal.get(GregorianCalendar.MONTH) + 1; // Add 1 to convert from 0-based months to 1-based months
    }

    private DataDate(GregorianCalendar cal) {
        day = cal.get(GregorianCalendar.DATE);
        month = cal.get(GregorianCalendar.MONTH) + 1; // Add 1 to convert from 0-based months to 1-based months
        year = cal.get(GregorianCalendar.YEAR);
        dayOfYear = cal.get(GregorianCalendar.DAY_OF_YEAR);
        hour=0;
    }

    //construct for date which contains hour
    public DataDate(int hour,int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour=hour;
        // Compute dayOfYear
        GregorianCalendar cal = getClearedCalendar();
        cal.set(year, month - 1, day); // Subtract 1 to convert from 1-based months to 0-based months
        dayOfYear = cal.get(GregorianCalendar.DAY_OF_YEAR);
    }

    /**
     * Gets a new DataDate representing today's date.
     */
    public static DataDate today() {
        return new DataDate(getTodayCalendar());
    }

    /**
     * Constructs a DataDate from a compact string (as returned by toCompactString()).
     */
    public static DataDate fromCompactString(String str) {
        final String PARSE_ERROR_MESSAGE = "Failed to parse compact data date string";

        final String[] parts = str.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException(PARSE_ERROR_MESSAGE);
        }

        final int year, month, day;
        try {
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            day = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(PARSE_ERROR_MESSAGE);
        }

        return new DataDate(day, month, year);
    }

    /**
     * Gets this instance's day (of the month).
     */
    public int getDay() {
        return day;
    }

    /**
     * Gets this instance's month. January is month 1.
     */
    public int getMonth() {
        return month;
    }

    /**
     * Gets this instance's year. This is the full year, not truncated.
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets this instance's day-of-year. January 1 is day 1 of the year.
     */
    public int getDayOfYear() {
        return dayOfYear;
    }

    /**
     * Gets this instance's hour.
     */
    public int getHour() {
        return hour;
    }

    public DataDate next() {
        return next(1);
    }

    public DataDate next(int days) {
        final GregorianCalendar cal = getCalendar();
        cal.add(GregorianCalendar.DAY_OF_YEAR, days);
        return new DataDate(cal);
    }

    public DataDate lastDayOfMonth() {
        final GregorianCalendar cal = getCalendar();
        // Jump back to the first day of this month
        cal.set(GregorianCalendar.DATE, 1);
        // Roll around to the last day of this month
        cal.roll(GregorianCalendar.DATE, -1);
        return new DataDate(cal);
    }

    /**
     * Returns the lesser of two DataDates, as defined by compareTo().
     */
    public static DataDate max(DataDate a, DataDate b) {
        if (a.compareTo(b) > 0) {
            return a;
        } else {
            return b;
        }
    }

    @Override
    public int compareTo(DataDate o) {
        // First, order by year
        if (year < o.year) {
            return -1;
        } else if (year > o.year) {
            return 1;
        }

        // Second, order by dayOfYear
        if (dayOfYear < o.dayOfYear) {
            return -1;
        } else if (dayOfYear > o.dayOfYear) {
            return 1;
        }

        // The objects are equal
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataDate) {
            return equals((DataDate)obj);
        } else {
            return false;
        }
    }

    public boolean equals(DataDate date) {
        return year == date.year && dayOfYear == date.dayOfYear;
    }

    @Override
    public int hashCode() {
        int hash = year;
        hash = hash * 17 + dayOfYear;
        return hash;
    }

    @Override
    public String toString() {
        return new StringBuilder()
        .append("{year: ").append(year)
        .append(", month: ").append(month)
        .append(", day: ").append(day)
        .append(", dayOfYear: ").append(dayOfYear)
        .append("}").toString();
    }

    public String toCompactString() {
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}