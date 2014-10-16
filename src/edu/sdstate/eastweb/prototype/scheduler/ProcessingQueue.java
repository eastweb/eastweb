package edu.sdstate.eastweb.prototype.scheduler;

import java.io.IOException;

import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.download.TrmmProduct;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;
import edu.sdstate.eastweb.prototype.scheduler.tasks.*;


public final class ProcessingQueue extends BaseTaskQueue {
    private enum Priority {
        Reprojection,
        IndexCalculation,
        ZonalSummary,
        DatabaseInsert
    }

    public ProcessingQueue(SchedulerFeedback feedback) {
        super(feedback);
    }

    @Override
    protected int getNumThreads() {
        return Runtime.getRuntime().availableProcessors();
    }



    /**
     * Enqueues a MODIS preparation task.
     */
    public void enqueuePrepareModis(ProjectInfo project, ModisProduct product,
            DataDate date, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Reprojection.ordinal(),
                new PrepareModisTask(project, product, date),
                continuation
        ));
    }

    public void enqueueModisClip(ProjectInfo project, ModisProduct product,
            DataDate date, String feature, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Reprojection.ordinal(),
                new ModisClipTask(project, product, date, feature),
                continuation
        ));
    }

    /**
     * Enqueues a TRMM reprojection task.
     */
    public void enqueueReprojectTrmm(ProjectInfo project, TrmmProduct product, DataDate date, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Reprojection.ordinal(),
                new GdalProjectTrmmTask(project, product, date),
                continuation
        ));
    }

    /**
     * Enqueues a Nldas reprojection task.
     */
    public void enqueueReprojectNldas(ProjectInfo project, DataDate date, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Reprojection.ordinal(),
                new ProjectNldasTask(project, date),
                continuation
        ));
    }


    public void enqueueTrmmClip(ProjectInfo project, TrmmProduct product,
            DataDate date, String feature, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Reprojection.ordinal(),
                new TrmmClipTask(project, product, date, feature),
                continuation
        ));
    }


    /**
     * Enqueues an ETo reprojection task.
     * @throws IOException
     */
    public void enqueueReprojectEto(ProjectInfo project, DataDate date,
            Runnable continuation) throws IOException {
        enqueue(new RunnableTaskQueueEntry(
                Priority.Reprojection.ordinal(),
                new ReprojectEtoTask(project, date),
                continuation
        ));
    }

    /**
     * Enqueues an index calculation task.
     */
    public void enqueueCalculateIndex(ProjectInfo project, EnvironmentalIndex index,
            DataDate date, String feature, Runnable continuation)
    {
        enqueue(new RunnableTaskQueueEntry(
                Priority.IndexCalculation.ordinal(),
                new GdalCalculateIndexTask(project, index, date, feature),
                continuation
        ));
    }

    /**
     * Enqueues a zonal summary task.
     */
    public void enqueueCalculateZonalStatistics(ProjectInfo project, EnvironmentalIndex index,
            DataDate date, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.ZonalSummary.ordinal(),
                new CalculateZonalStatisticsTask(project, index, date),
                continuation
        ));
    }

    /**
     * Enqueues a database insert task.
     */
    public void enqueueUploadResults(ProjectInfo project, EnvironmentalIndex index,
            DataDate date, Runnable continuation) {
        enqueue(new RunnableTaskQueueEntry(
                Priority.DatabaseInsert.ordinal(),
                new UploadResultsTask(project, index, date),
                continuation
        ));
    }


}