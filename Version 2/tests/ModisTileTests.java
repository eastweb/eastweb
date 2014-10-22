package edu.sdstate.eastweb.prototype.tests;

import java.io.IOException;
import org.junit.Test;
import org.w3c.dom.*;
import edu.sdstate.eastweb.prototype.ModisTile;
import edu.sdstate.eastweb.prototype.util.XmlUtils;
import static org.junit.Assert.*;

public class ModisTileTests extends MetadataTests {
    @Test
    public void testXml() throws IOException {
        final Document doc = XmlUtils.newDocument("test");
        for (int i = 0; i < 1000; ++i) {
            final ModisTile ref = random();
            assertEquals(ref, ModisTile.fromXml(ref.toXml(doc)));
        }
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static ModisTile random() {
        final int hTile = sRandom.nextInt(ModisTile.HORZ_MAX + 1);
        final int vTile = sRandom.nextInt(ModisTile.VERT_MAX + 1);

        final ModisTile obj = new ModisTile(hTile, vTile);

        assertEquals(hTile, obj.getHTile());
        assertEquals(vTile, obj.getVTile());

        return obj;
    }
}