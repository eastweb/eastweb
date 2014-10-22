package edu.sdstate.eastweb.prototype.tests;

import java.util.*;
import org.junit.Test;
import edu.sdstate.eastweb.prototype.DataDate;
import static org.junit.Assert.*;

public class DataDateTests extends MetadataTests {
    @Test
    public void testDayMonthYearConstructor() {
        final DataDate date = new DataDate(26, 6, 2002); // June 26, 2002; day 177
        assertEquals("Day is incorrect", 26, date.getDay());
        assertEquals("Month is incorrect", 6, date.getMonth());
        assertEquals("Year is incorrect", 2002, date.getYear());
        assertEquals("DayOfYear is incorrect", 177, date.getDayOfYear());
    }

    @Test
    public void testDayOfYearYearConstructor() {
        final DataDate date = new DataDate(177, 2002); // June 26, 2002; day 177
        assertEquals("Day is incorrect", 26, date.getDay());
        assertEquals("Month is incorrect", 6, date.getMonth());
        assertEquals("Year is incorrect", 2002, date.getYear());
        assertEquals("DayOfYear is incorrect", 177, date.getDayOfYear());
    }

    @Test
    public void testJanuary1IsDay1OfYear() {
        assertEquals("January 1 is not day 1 of the year", 1, new DataDate(1, 1, 2011).getDayOfYear());
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, new DataDate(26, 6, 2002).compareTo(new DataDate(177, 2002)));
        assertEquals(1, new DataDate(178, 2002).compareTo(new DataDate(177, 2002)));
        assertEquals(-1, new DataDate(176, 2002).compareTo(new DataDate(177, 2002)));
        assertEquals(1, new DataDate(177, 2003).compareTo(new DataDate(177, 2002)));
        assertEquals(-1, new DataDate(177, 2001).compareTo(new DataDate(177, 2002)));
    }

    @Test
    public void testNext() {
        assertEquals(new DataDate(177, 2002), new DataDate(176, 2002).next());
        assertEquals(new DataDate(1, 2003), new DataDate(31, 12, 2002).next());
    }

    @Test
    public void testLastDayOfMonth() {
        assertEquals(new DataDate(28, 2, 2011), new DataDate(12, 2, 2011).lastDayOfMonth());
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static DataDate random() {
        if (sRandom.nextBoolean()) {
            final int dayOfYear = sRandom.nextInt(365) + 1;
            final int year = 2005 + sRandom.nextInt(5);

            final DataDate obj = new DataDate(dayOfYear, year);

            assertEquals(dayOfYear, obj.getDayOfYear());
            assertEquals(year, obj.getYear());

            return obj;
        } else {
            final int day = sRandom.nextInt(28) + 1;
            final int month = sRandom.nextInt(12) + 1;
            final int year = 2005 + sRandom.nextInt(5);

            final DataDate obj = new DataDate(day, month, year);

            assertEquals(day, obj.getDay());
            assertEquals(month, obj.getMonth());
            assertEquals(year, obj.getYear());

            return obj;
        }
    }

    public static List<DataDate> randomList() {
        final List<DataDate> list = new ArrayList<DataDate>();

        final int size = sRandom.nextInt(10) + 10;
        for (int i = 0; i < size; ++i) {
            list.add(random());
        }

        return list;
    }
}