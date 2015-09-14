package edu.sdstate.eastweb.prototype.indices;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.ProjectInfo;

public class DefaultIndices extends IndicesFramework{

    public DefaultIndices(ProjectInfo project, EnvironmentalIndex index, DataDate date, String feature) throws Exception
    {
        // this will create all indicies and new indicies that are implemented using GdaSimpleIndexCalculator
        super(project, index, date, feature);
    }

    @Override
    public void AddMaps() throws Exception{
        // Add all the new Indices calculation
    }

}
