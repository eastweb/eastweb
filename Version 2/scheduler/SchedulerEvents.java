package edu.sdstate.eastweb.prototype.scheduler;

import java.util.List;

/**
 * Defines the event registration part of the scheduler interface.
 * 
 * @author Michael VanBemmel
 */
public interface SchedulerEvents {
    public static interface SchedulerEventListener {
        /**
         * Called when a new task has begun and should be displayed.
         * @param taskName Unique task name (not null)
         * @param reportsProgress True if taskUpdated() may be called with this task name (progress
         *      should start at zero); false if taskUpdated() will not be called with this task name
         *      (progress should be reported as indeterminate)
         */
        public abstract void newTask(String taskName, boolean reportsProgress);

        /**
         * Called when a task has progress to report. This method will only be called after a
         * call to newTask() with the same task name and {@code reportsProgress} set to true.
         * @param taskName Unique task name (not null)
         * @param progress Completed progress units (non-negative and {@code <= total})
         * @param total Total progress units
         */
        public abstract void taskUpdated(String taskName, int progress, int total);

        /**
         * Called when a task has completed and should be hidden.
         * @param taskName Unique task name (not null)
         */
        public abstract void taskCompleted(String taskName);

        /**
         * Called when a task has failed and should be hidden. A message should be extracted from
         * the {@code cause} parameter and collected in a log display of some kind.
         * @param taskName Unique task name (not null)
         * @param cause Throwable that caused the failure
         */
        public abstract void taskFailed(String taskName, Throwable cause);

        /**
         * Called when a task group has progress to report.
         * @param taskGroupName Unique task group name (not null); this will be one of the strings
         *      in the list returned by {@code getTaskGroupNames()}
         * @param progress Completed progress units (non-negative and {@code <= total})
         * @param total Total progress units
         */
        public abstract void taskGroupUpdated(String taskGroupName, int progress, int total);
    }

    /**
     * Adds a listener to handle scheduler events. Note that the listener's methods will be called
     * from one of the scheduler's threads.
     * @param listener
     */
    public void addSchedulerEventListener(SchedulerEventListener listener);

    /**
     * Removes a listener that was previously added.
     * @param listener
     */
    public void removeSchedulerEventListener(SchedulerEventListener listener);

    /**
     * Gets an unmodifiable list containing the name of each task group. Only strings in this list
     * will be passed as the {@code taskGroupName} parameter of {@code taskGroupUpdated()}
     */
    public List<String> getTaskGroupNames();
}