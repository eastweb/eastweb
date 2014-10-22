package edu.sdstate.eastweb.prototype.download.tests;

import org.junit.Test;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.tests.MetadataTests;

public final class ModisProductTests extends MetadataTests {
    @Test
    public void testRandom() {
        for (int i = 0; i < 10000; ++i) {
            random();
        }
    }

    public static ModisProduct random() {
        return sRandom.nextBoolean() ? ModisProduct.NBAR : ModisProduct.LST;
    }
}