package edu.sdstate.eastweb.prototype.indices.tests;

import java.io.IOException;
import java.lang.reflect.*;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.MetaData;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;
import edu.sdstate.eastweb.prototype.indices.GdalETACalculator;
import edu.sdstate.eastweb.prototype.indices.IndexCalculator;


public class TestMain {

    public static void main(String[] args) throws NumberFormatException, IOException  {
        try {



            String haha = GdalETACalculator.class.getClass().getPackage().getName();

            Class<?> clazz = Class.forName("edu.sdstate.eastweb.prototype.indices." + MetaData.GetInstance().IndicesMetaData);
            Constructor<?> ctor = clazz.getConstructor(ProjectInfo.class, DataDate.class, String.class, EnvironmentalIndex.class);
            Object object = ctor.newInstance(new Object[] { null, null, null, null });

            //Field j = indices.getClass().getField(mIndex.toString());

            IndexCalculator temp = (IndexCalculator) object;

            //Field j = test.getClass().getField(temp.IndicesMetaData);

            // IndexCalculator temp = (IndexCalculator) j.get(test);
            //final Value v = Value.class.getConstructor(
            //      int.class, int.class, double.class).newInstance(_xval1,_xval2,_pval);

            //int temp4 = (Integer)j.get(test);
            int temp3 = 5;




        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
