package version2.prototype.EastWebUI;

import version2.prototype.Scheduler.Scheduler;
import version2.prototype.Scheduler.SchedulerData;

public class Test {

    public static void main(String[] args) throws Exception {

        SchedulerData data = new SchedulerData(); // TODO: this will be replace by user interface

        Scheduler.getInstance(data).run();
    }
}
