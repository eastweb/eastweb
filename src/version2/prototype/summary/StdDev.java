package version2.prototype.summary;

import java.util.ArrayList;
import java.util.Map;

public class StdDev extends SummarySingleton {
    private SummarySingleton sqrSum;
    private SummarySingleton count;
    private SummarySingleton mean;

    public StdDev(SummariesCollection col) {
        super(col);
    }

    @Override
    public void put(int index, double value) {
        sqrSum.put(index, value);
        count.put(index, value);
        mean.put(index, value);
    }

    @Override
    public Map<Integer, Double> getResult() {
        if(map.size() == 0 || map.size() < count.map.size()){
            Map<Integer, Double> sqrSumRs = sqrSum.getResult();
            Map<Integer, Double> countRs = count.getResult();
            Map<Integer, Double> meanRs = mean.getResult();

            for(int i=0; i < countRs.size(); i++){
                map.put(i, Math.sqrt((sqrSumRs.get(i)/countRs.get(i)) - (meanRs.get(i) * meanRs.get(i))));
            }
        }
        return map;
    }

    @Override
    public ArrayList<SummarySingleton> getDistinctLeaflets() {
        ArrayList<SummarySingleton> temp = new ArrayList<SummarySingleton>(3);
        temp.add(count);
        temp.add(sqrSum);
        ArrayList<SummarySingleton> fromMean = mean.getDistinctLeaflets();
        if(fromMean.get(0).getClass().getSimpleName().equalsIgnoreCase("sum")) {
            temp.add(fromMean.get(0));
        } else {
            temp.add(fromMean.get(1));
        }
        return temp;
    }

    @Override
    protected void registerDependencies() {
        sqrSum = new SqrSum(col);
        sqrSum = col.register(new SummaryNameInstancePair(sqrSum.getCanonicalName(), sqrSum));

        count = new Count(col);
        count = col.register(new SummaryNameInstancePair(count.getCanonicalName(), count));

        mean = new Mean(col);
        mean = col.register(new SummaryNameInstancePair(mean.getCanonicalName(), mean));
    }

}
