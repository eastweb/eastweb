package edu.sdstate.eastweb.prototype.indices;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.ProjectInfo;

public class SampleIncidiesImplementation extends IndicesFramework{

    public SampleIncidiesImplementation(ProjectInfo project, EnvironmentalIndex index, DataDate date, String feature) throws Exception
    {
        // this will create all indicies and new indicies that are implemented using GdaSimpleIndexCalculator
        super(project, index, date, feature);
    }

    @Override
    public void SetNewIndices() throws Exception{
        // Add all the new Indices calculation
    }

}
