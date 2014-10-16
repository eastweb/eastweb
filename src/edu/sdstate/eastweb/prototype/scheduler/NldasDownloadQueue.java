package edu.sdstate.eastweb.prototype.scheduler;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.cache.DateCache;
import edu.sdstate.eastweb.prototype.scheduler.tasks.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.Action;

public class NldasDownloadQueue extends BaseTaskQueue{
    private enum Priority {
        DateCache,
        Download
    }

    public NldasDownloadQueue(SchedulerFeedback feedback) {
        super(feedback);
    }

    @Override
    protected int getNumThreads() {
        return 2;
    }

    /**
     * Enqueues a check date cache task.
     */
    public void enqueueCheckDateCache(DataDate startDate, Action<DateCache> continuation) {
        enqueue(new CallableTaskQueueEntry<DateCache>(
                Priority.DateCache.ordinal(),
                new UpdateNldasDateCacheTask(startDate),
                continuation
        ));
    }

    /**
     * Enqueues a download task.
     */
    public void enqueueDownload(DataDate date, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Download.ordinal(),
                new DownloadNldasTask(date),
                continuation
        ));
    }
}
