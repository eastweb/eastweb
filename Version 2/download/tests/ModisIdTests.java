package edu.sdstate.eastweb.prototype.download.tests;

import java.io.IOException;
import java.util.*;
import org.junit.Test;
import org.w3c.dom.Document;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.tests.*;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public final class ModisIdTests extends MetadataTests{
    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final ModisId ref = random();
            assertEquals(ref, ModisId.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static ModisId random() {
        final ModisProduct product = ModisProductTests.random();
        final DataDate date = DataDateTests.random();
        final ModisTile tile = ModisTileTests.random();
        final DataDate processed = DataDateTests.random();
        final ModisId obj = new ModisId(product, date, tile, processed);

        assertEquals(product, obj.getProduct());
        assertEquals(date, obj.getDate());
        assertEquals(tile, obj.getTile());
        assertEquals(processed, obj.getProcessed());
        return obj;
    }

    public static List<ModisId> randomList() {
        final List<ModisId> list = new ArrayList<ModisId>();

        final int size = sRandom.nextInt(10) + 10;
        for (int i = 0; i < size; ++i) {
            list.add(random());
        }

        return list;
    }
}