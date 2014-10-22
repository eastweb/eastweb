package edu.sdstate.eastweb.prototype.scheduler;

public interface Scheduler extends SchedulerEvents {
    void start();
    void stop();
    void join();
}