/**
 * 
 */
package version2.prototype.summary;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Mike DeVos
 *
 */
public class SummariesCollectionTest {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    /**
     * Test method for {@link version2.prototype.summary.SummariesCollection#SummariesCollection(java.lang.String[])}.
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    @Test
    public void testSummariesCollection() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        SummariesCollection col;

        col = new SummariesCollection("");

    }

    /**
     * Test method for {@link version2.prototype.summary.SummariesCollection#lookup(java.lang.String)}.
     */
    @Test
    public void testLookup() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link version2.prototype.summary.SummariesCollection#put(int, double)}.
     */
    @Test
    public void testPut() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link version2.prototype.summary.SummariesCollection#register(version2.prototype.summary.SummaryNameInstancePair)}.
     */
    @Test
    public void testRegister() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link version2.prototype.summary.SummariesCollection#getResults()}.
     */
    @Test
    public void testGetResults() {
        fail("Not yet implemented");
    }

}
