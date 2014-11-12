package version2.prototype.summary;

import java.util.Map;

public class SummaryNameResultPair {
    public SummaryNameResultPair(String simpleName, Map<Integer, Double> result){
        name = simpleName;
        this.result = result;
    }

    public String getSimpleName(){ return name; }

    public Map<Integer, Double> getResult(){ return result; }

    @Override
    public String toString(){
        return name + ": " + result;
    }

    public String toString(String delimeter){
        return name + delimeter + " " + result;
    }

    private String name;
    private Map<Integer, Double> result;
}
