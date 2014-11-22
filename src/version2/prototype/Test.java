package version2.prototype;

import version2.prototype.Scheduler.Scheduler;

public class Test {

    public static void main(String[] args) throws Exception {

        InitializeMockData data = new InitializeMockData(); // TODO: this will be replace by user interface

        Scheduler.getInstance(data).run();
    }


}
