package version2.prototype.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class SummarySingleton {
    public SummarySingleton(SummariesCollection col){
        map = new HashMap<Integer, Double>();
        this.col = col;
        canonicalName = this.getClass().getCanonicalName();
        registerDependencies();
    }

    protected void registerDependencies(){    }

    public abstract void put(int index, double value);

    public abstract Map<Integer, Double> getResult();

    public String getCanonicalName(){
        return canonicalName;
    }

    public abstract ArrayList<SummarySingleton> getDistinctLeaflets();

    protected String canonicalName;
    protected Map<Integer, Double> map;
    protected SummariesCollection col;
}
