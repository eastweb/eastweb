package version2.prototype.summary;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class SummariesCollection {
    public SummariesCollection(ArrayList<String> summaryNames) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
    InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        summaries = new ArrayList<SummarySingleton>();
        registry = new ArrayList<SummaryNameInstancePair>();
        SummarySingleton temp = null;
        String canonicalPath = this.getClass().getCanonicalName().substring(0, this.getClass().getCanonicalName().lastIndexOf(".") + 1);
        boolean alreadyRegistered = false;

        if(summaryNames.size() > 0){
            for(String name : summaryNames){
                if(!name.equals("")){
                    Class<?> summary =  Class.forName(canonicalPath + name);
                    Constructor<?> summaryCons = summary.getConstructor(SummariesCollection.class);

                    if(registry.size() == 0){
                        temp = (SummarySingleton)summaryCons.newInstance(this);
                        summaries.add(temp);
                        registry.add(new SummaryNameInstancePair(temp.getCanonicalName(), temp));
                    }
                    else{
                        temp = null;
                        alreadyRegistered = false;
                        for(SummaryNameInstancePair pair : registry){
                            if(pair.getCanonicalName().equals(canonicalPath + name)) {
                                alreadyRegistered = true;
                                temp = pair.getInstance();
                            }
                        }
                        if(alreadyRegistered){
                            summaries.add(temp);
                        }
                        else {
                            temp = (SummarySingleton)summaryCons.newInstance(this);
                            summaries.add(temp);
                            registry.add(new SummaryNameInstancePair(temp.getCanonicalName(), temp));
                        }
                    }
                }
            }
        }
    }

    public SummarySingleton lookup(String canonicalName){
        boolean found = false;
        int i = 0;
        SummarySingleton instance = null;
        SummaryNameInstancePair pair = null;

        while(!found && (i < registry.size())){
            pair = registry.get(i);
            if(pair.getCanonicalName().equalsIgnoreCase(canonicalName)){
                instance = pair.getInstance();
                found = true;
            }
            i = i + 1;
        }

        return instance;
    }

    public void put(int index, double value){
        ArrayList<SummarySingleton> leavesFromSummaries = new ArrayList<SummarySingleton>();
        ArrayList<SummarySingleton> temp = null;
        for(SummarySingleton summary : summaries){
            temp = summary.getDistinctLeaflets();
            if(temp != null){
                for(SummarySingleton s : temp){
                    if(!leavesFromSummaries.contains(s)) {
                        leavesFromSummaries.add(s);
                    }
                }
            }
        }

        for(SummarySingleton summary : leavesFromSummaries){
            summary.put(index, value);
        }
    }

    public SummarySingleton register(SummaryNameInstancePair pair){
        SummarySingleton temp = lookup(pair.getCanonicalName());
        if(temp == null){
            registry.add(pair);
            temp = pair.getInstance();
        }
        return temp;
    }

    public ArrayList<SummaryNameResultPair> getResults(){
        ArrayList<SummaryNameResultPair> results = new ArrayList<SummaryNameResultPair>();
        for(SummarySingleton summary : summaries){
            results.add(new SummaryNameResultPair(summary.getClass().getSimpleName(), summary.getResult()));
        }
        return results;
    }

    private ArrayList<SummaryNameInstancePair> registry;
    private ArrayList<SummarySingleton> summaries;
}
