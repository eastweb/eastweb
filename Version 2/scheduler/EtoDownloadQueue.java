package edu.sdstate.eastweb.prototype.scheduler;

import java.util.List;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.EtoArchive;
import edu.sdstate.eastweb.prototype.download.cache.EtoArchiveCache;
import edu.sdstate.eastweb.prototype.scheduler.framework.Action;
import edu.sdstate.eastweb.prototype.scheduler.tasks.*;

public final class EtoDownloadQueue extends BaseTaskQueue {
    private enum Priority {
        ArchiveCache,
        Download
    }

    public EtoDownloadQueue(SchedulerFeedback feedback) {
        super(feedback);
    }

    @Override
    protected int getNumThreads() {
        return 2;
    }

    /**
     * Enqueues a check archive cache task.
     */
    public void enqueueCheckArchiveCache(DataDate startDate,
            Action<EtoArchiveCache> continuation) {
        enqueue(new CallableTaskQueueEntry<EtoArchiveCache>(
                Priority.ArchiveCache.ordinal(),
                new UpdateEtoArchiveCacheTask(startDate),
                continuation
        ));
    }

    /**
     * Enqueues a download task.
     */
    public void enqueueDownload(EtoArchive archive, Action<List<DataDate>> continuation) {
        enqueue(new CallableTaskQueueEntry<List<DataDate>>(
                Priority.Download.ordinal(),
                new DownloadEtoTask(archive),
                continuation
        ));
    }
}