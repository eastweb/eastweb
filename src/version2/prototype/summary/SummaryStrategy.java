package version2.prototype.summary;

import java.util.Map;

public interface SummaryStrategy {

    void put(Map<Integer, Double> map, int index, double value);
}
