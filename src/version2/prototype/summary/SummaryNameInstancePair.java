package version2.prototype.summary;

public class SummaryNameInstancePair {
    public SummaryNameInstancePair(String canonicalName, SummarySingleton instance){
        this.canonicalName = canonicalName;
        this.instance = instance;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public SummarySingleton getInstance() {
        return instance;
    }

    private String canonicalName;
    private SummarySingleton instance;
}
