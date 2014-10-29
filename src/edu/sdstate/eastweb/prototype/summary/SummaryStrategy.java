package edu.sdstate.eastweb.prototype.summary;

import java.util.Map;

public interface SummaryStrategy {

    void put(Map<Integer, Double> map, int index, double value);
}
