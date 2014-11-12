package version2.prototype.summary;

import java.util.ArrayList;
import java.util.Map;

public class Count extends SummarySingleton {

    public Count(SummariesCollection col) {
        super(col);
    }

    @Override
    public void put(int index, double value) {
        if(map.get(index) == null) {
            map.put(index, 1.0);
        } else {
            map.put(index, map.get(index) + 1);
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
