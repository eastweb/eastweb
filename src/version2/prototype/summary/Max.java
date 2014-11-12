package version2.prototype.summary;

import java.util.ArrayList;
import java.util.Map;

public class Max extends SummarySingleton {

    public Max(SummariesCollection col) {
        super(col);
    }

    @Override
    public void put(int index, double value) {
        if(map.get(index) == null) {
            map.put(index, value);
        } else if(map.get(index) < value) {
            map.put(index, value);
        }
    }

    @Override
    public Map<Integer, Double> getResult() {
        return map;
    }

    @Override
    public ArrayList<SummarySingleton> getDistinctLeaflets() {
        ArrayList<SummarySingleton> temp = new ArrayList<SummarySingleton>();
        temp.add(this);
        return temp;
    }
}
