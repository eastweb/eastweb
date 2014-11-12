package version2.prototype.summary;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Tester {
    public static void main(String[] args) {
        SummariesCollection col = null;
        try {
            col = new SummariesCollection("Count", "Sum", "Mean", "StdDev");
            //testData1(col);
            testData2(col);
            ArrayList<SummaryNameResultPair> results = col.getResults();
            for(SummaryNameResultPair pair : results){
                System.out.println(pair.getSimpleName() + ": " + pair.getResult().toString());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param col
     */
    private static void testData1(SummariesCollection col) {
        col.put(0, 1.0);
        col.put(1, 2.0);
        col.put(2, 3.0);
        col.put(3, 4.0);
        col.put(4, 5.0);
        col.put(5, 6.0);

        col.put(0, 1.0);
        col.put(1, 2.0);
        col.put(2, 3.0);
        col.put(3, 4.0);
        col.put(4, 5.0);
        col.put(5, 6.0);

        col.put(0, 1.0);
        col.put(1, 2.0);
        col.put(2, 3.0);
        col.put(3, 4.0);
        col.put(4, 5.0);
        col.put(5, 6.0);
    }

    /**
     * @param col
     */
    private static void testData2(SummariesCollection col) {
        col.put(0, 1.0);
        col.put(1, 1.0);
        col.put(2, 1.0);
        col.put(3, 1.0);
        col.put(4, 1.0);
        col.put(5, 1.0);

        col.put(0, 2.0);
        col.put(1, 2.0);
        col.put(2, 2.0);
        col.put(3, 2.0);
        col.put(4, 2.0);
        col.put(5, 2.0);

        col.put(0, 3.0);
        col.put(1, 3.0);
        col.put(2, 3.0);
        col.put(3, 3.0);
        col.put(4, 3.0);
        col.put(5, 3.0);

        col.put(0, 4.0);
    }
}
