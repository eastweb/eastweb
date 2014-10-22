package edu.sdstate.eastweb.prototype.scheduler;

import edu.sdstate.eastweb.prototype.scheduler.framework.Task;

public interface SchedulerFeedback {
    void newTask(Task task);
    void taskCompleted(Task task);
    void taskFailed(Task task, Throwable cause);
}