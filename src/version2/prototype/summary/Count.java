package version2.prototype.summary;

import java.util.Map;

public class Count implements SummaryStrategy {

    @Override
    public void put(Map<Integer, Double> map, int index, double value) {
        if(map.get(index) == null) {
            map.put(index, 1.0);
        } else {
            map.put(index, map.get(index) + 1);
        }
    }

}
