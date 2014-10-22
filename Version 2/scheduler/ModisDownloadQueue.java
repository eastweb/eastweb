package edu.sdstate.eastweb.prototype.scheduler;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.download.cache.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.Action;
import edu.sdstate.eastweb.prototype.scheduler.tasks.*;

public final class ModisDownloadQueue extends BaseTaskQueue {
    private enum Priority {
        DateCache,
        TileCache,
        Download
    }

    public ModisDownloadQueue(SchedulerFeedback feedback) {
        super(feedback);
    }

    @Override
    protected int getNumThreads() {
        return 4; // Experimentally determined limit set by LP DAAC servers is 6, but varies
    }

    /**
     * Enqueues a check date cache task.
     */
    public void enqueueCheckDateCache(ModisProduct product, DataDate startDate,
            Action<DateCache> continuation) {
        enqueue(new CallableTaskQueueEntry<DateCache>(
                Priority.DateCache.ordinal(),
                new UpdateModisDateCacheTask(product, startDate),
                continuation
                ));
    }

    /**
     * Enqueues a check tile cache task.
     */
    public void enqueueCheckTileCache(ModisProduct product, DataDate date,
            Action<ModisTileCache> continuation) {
        enqueue(new CallableTaskQueueEntry<ModisTileCache>(
                Priority.TileCache.ordinal(),
                new UpdateModisTileCacheTask(product, date),
                continuation
                ));
    }

    /**
     * Enqueues a download task.
     */
    public void enqueueDownload(ModisId modisId, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Download.ordinal(),
                new DownloadModisTask(modisId),
                continuation
                ));
    }
}