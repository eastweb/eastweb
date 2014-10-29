package edu.sdstate.eastweb.prototype.summary;

import java.util.Map;

public class Sum implements SummaryStrategy {

    @Override
    public void put(Map<Integer, Double> map, int index, double value) {
        if(map.get(index) == null) {
            map.put(index, value);
        } else {
            map.put(index, map.get(index) + value);
        }
    }

}
