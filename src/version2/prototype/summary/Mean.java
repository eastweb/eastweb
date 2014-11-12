package version2.prototype.summary;

import java.util.ArrayList;
import java.util.Map;

public class Mean extends SummarySingleton {
    private SummarySingleton sum;
    private SummarySingleton count;

    public Mean(SummariesCollection col) {
        super(col);
    }

    @Override
    public void put(int index, double value) {
        sum.put(index, value);
        count.put(index, value);
    }

    @Override
    public Map<Integer, Double> getResult() {
        if(map.size() == 0 || map.size() < count.map.size()){
            Map<Integer, Double> sumRs = sum.getResult();
            Map<Integer, Double> countRs = count.getResult();

            for(int i=0; i < countRs.size(); i++){
                map.put(i, sumRs.get(i)/countRs.get(i));
            }
        }
        return map;
    }

    @Override
    public ArrayList<SummarySingleton> getDistinctLeaflets() {
        ArrayList<SummarySingleton> temp = new ArrayList<SummarySingleton>(2);
        temp.add(count);
        temp.add(sum);
        return temp;
    }

    @Override
    protected void registerDependencies() {
        sum = new Sum(col);
        sum = col.register(new SummaryNameInstancePair(sum.getCanonicalName(), sum));

        count = new Count(col);
        count = col.register(new SummaryNameInstancePair(count.getCanonicalName(), count));
    }
}
