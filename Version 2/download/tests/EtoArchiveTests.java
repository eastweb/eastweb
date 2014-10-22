package edu.sdstate.eastweb.prototype.download.tests;

import java.io.IOException;
import java.util.*;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.download.EtoArchive;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public class EtoArchiveTests extends MetadataTests {
    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final EtoArchive ref = random();
            assertEquals(ref, EtoArchive.fromXml(ref.toXml(doc)));
        }
    }

    public static EtoArchive random() {
        final int branch = sRandom.nextInt(3);
        if (branch == 0) {
            final int year = 2005 + sRandom.nextInt(5);
            final EtoArchive obj = EtoArchive.yearly(year);

            assertEquals(year, obj.getYear());
            return obj;
        } else if (branch == 1) {
            final int year = 2005 + sRandom.nextInt(5);
            final int month = sRandom.nextInt(12) + 1;
            final EtoArchive obj = EtoArchive.monthly(year, month);

            assertEquals(year, obj.getYear());
            assertEquals(month, obj.getMonth());
            return obj;
        } else if (branch == 2) {
            final int year = 2005 + sRandom.nextInt(5);
            final int month = sRandom.nextInt(12) + 1;
            final int day = sRandom.nextInt(365) + 1;
            final EtoArchive obj = EtoArchive.daily(year, month, day);

            assertEquals(year, obj.getYear());
            assertEquals(month, obj.getMonth());
            assertEquals(day, obj.getDay());
            return obj;
        } else {
            fail();
            return null;
        }
    }

    public static List<EtoArchive> randomList() {
        final List<EtoArchive> list = new ArrayList<EtoArchive>();

        final int size = sRandom.nextInt(10) + 10;
        for (int i = 0; i < size; ++i) {
            list.add(random());
        }

        return list;
    }
}