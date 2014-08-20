package edu.sdstate.eastweb.prototype.indices;

import java.lang.reflect.*;

public class TestMain {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        try {
            SampleIncidiesImplementation test = new SampleIncidiesImplementation(null, null, null, null);



            Field j = test.getClass().getField("temp");

            // IndexCalculator temp = (IndexCalculator) j.get(test);

            int temp4 = (Integer)j.get(test);
            int temp3 = 5;




        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
