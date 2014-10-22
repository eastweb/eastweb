package edu.sdstate.eastweb.prototype.scheduler;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.download.cache.DateCache;
import edu.sdstate.eastweb.prototype.scheduler.tasks.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.Action;

public final class TrmmDownloadQueue extends BaseTaskQueue {
    private enum Priority {
        DateCache,
        Download
    }

    public TrmmDownloadQueue(SchedulerFeedback feedback) {
        super(feedback);
    }

    @Override
    protected int getNumThreads() {
        return 2;
    }

    /**
     * Enqueues a check date cache task.
     */
    public void enqueueCheckDateCache(TrmmProduct product, DataDate startDate, Action<DateCache> continuation) {
        enqueue(new CallableTaskQueueEntry<DateCache>(
                Priority.DateCache.ordinal(),
                new UpdateTrmmDateCacheTask(product, startDate),
                continuation
                ));
    }

    /**
     * Enqueues a download task.
     */
    public void enqueueDownload(TrmmProduct product, DataDate date, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Download.ordinal(),
                new DownloadTrmmTask(product, date),
                continuation
                ));
    }
}