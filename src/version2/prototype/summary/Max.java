package version2.prototype.summary;

import java.util.Map;

public class Max implements SummaryStrategy {

    @Override
    public void put(Map<Integer, Double> map, int index, double value) {
        if(map.get(index) == null) {
            map.put(index, value);
        } else if(map.get(index) < value) {
            map.put(index, value);
        }
    }

}
