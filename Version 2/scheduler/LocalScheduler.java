package edu.sdstate.eastweb.prototype.scheduler;

import java.util.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.download.cache.*;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;
import edu.sdstate.eastweb.prototype.scheduler.framework.*;

public final class LocalScheduler implements Scheduler, SchedulerFeedback {
    // Task group names (for progress events)
    private static final String UPDATE_CACHE_GROUP_NAME = "Update download caches";
    private static final String DOWNLOAD_DATA_GROUP_NAME = "Download data";
    private static final String REPROJECT_MODIS_GROUP_NAME = "Reproject and mosaic MODIS tiles";
    private static final String REPROJECT_TRMM_GROUP_NAME = "Reproject TRMM data";
    private static final String REPROJECT_ETO_GROUP_NAME = "Reproject and composite ETo data";
    private static final String CALCULATE_INDICES_GROUP_NAME = "Calculate environmental indices";
    private static final String CALCULATE_ZONAL_STATS_GROUP_NAME = "Calculate zonal statistics";
    private static final String UPLOAD_RESULTS_GROUP_NAME = "Upload results to the database";

    // Task group counters
    private final Object mTaskGroupLock = new Object();
    private final Map<String, Integer> mTaskGroupProgresses = new HashMap<String, Integer>();
    private final Map<String, Integer> mTaskGroupTotals = new HashMap<String, Integer>();

    // Listener list for new/updated/completed task events
    private final List<SchedulerEventListener> mListeners = new ArrayList<SchedulerEventListener>();

    // Task queues
    private final ModisDownloadQueue mModisDownloadQueue = new ModisDownloadQueue(this);
    private final TrmmDownloadQueue mTrmmDownloadQueue = new TrmmDownloadQueue(this);
    private final EtoDownloadQueue mEtoDownloadQueue = new EtoDownloadQueue(this);
    private final ProcessingQueue mProcessingQueue = new ProcessingQueue(this);

    // Task convergence points -- multiple continuations will trigger individual new tasks
    private final ModisReprojectionDependencyTracker mModisReprojectionDependencyTracker = new ModisReprojectionDependencyTracker();
    private final EtaDependencyTracker mEtaDependencyTracker = new EtaDependencyTracker();

    // Configuration
    private final List<ProjectInfo> mProjects;
    private final DataDate mOldestStartDate;
    private final Set<ModisTile> mModisTileUnion;

    private boolean mStarted = false;

    public LocalScheduler(ProjectInfo[] projects) {
        // Filter out inactive projects
        List<ProjectInfo> active = new ArrayList<ProjectInfo>();
        for (ProjectInfo project : projects) {
            if (project.isActive()) {
                active.add(project);
            }
        }

        // Make an unmodifiable copy of the provided projects array
        mProjects = Collections.unmodifiableList(
                new ArrayList<ProjectInfo>(active));

        // Find the oldest start date and build the union of all projects' MODIS tile sets
        DataDate oldestStartDate = null;
        final Set<ModisTile> modisTileUnion = new HashSet<ModisTile>();
        for (ProjectInfo project : mProjects) {
            final DataDate startDate = project.getStartDate();
            if (oldestStartDate == null || oldestStartDate.compareTo(startDate) > 0) {
                oldestStartDate = startDate;
            }

            Collections.addAll(modisTileUnion, project.getModisTiles());
        }
        mOldestStartDate = oldestStartDate;
        mModisTileUnion = Collections.unmodifiableSet(modisTileUnion);

        setupSubscriptions();
    }

    @Override
    public void start() {
        if (!mStarted) {
            mModisDownloadQueue.start();
            mTrmmDownloadQueue.start();
            mEtoDownloadQueue.start();
            mProcessingQueue.start();
            if (mOldestStartDate != null) {
                enqueueInitialTasks();
            }
            mStarted = true;
        }
    }

    @Override
    public void stop() {
        mModisDownloadQueue.stop();
        mTrmmDownloadQueue.stop();
        mEtoDownloadQueue.stop();
        mProcessingQueue.stop();
    }

    @Override
    public void join() {
        mModisDownloadQueue.join();
        mTrmmDownloadQueue.join();
        mEtoDownloadQueue.join();
        mProcessingQueue.join();
    }

    @Override
    public void addSchedulerEventListener(SchedulerEventListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    private void onNewTask(String taskName, boolean reportsProgress) {
        synchronized (mListeners) {
            for (SchedulerEventListener listener : mListeners) {
                listener.newTask(taskName, reportsProgress);
            }
        }
    }

    //    private void onTaskUpdated(String taskName, int progress, int total) {
    //        synchronized (mListeners) {
    //            for (SchedulerEventListener listener : mListeners) {
    //                listener.taskUpdated(taskName, progress, total);
    //            }
    //        }
    //    }

    private void onTaskCompleted(String taskName) {
        synchronized (mListeners) {
            for (SchedulerEventListener listener : mListeners) {
                listener.taskCompleted(taskName);
            }
        }
    }

    private void onTaskFailed(String taskName, Throwable cause) {
        synchronized (mListeners) {
            for (SchedulerEventListener listener : mListeners) {
                listener.taskFailed(taskName, cause);
            }
        }
    }

    private void incrementTaskGroup(String taskGroupName, int deltaProgress, int deltaTotal) {
        int progress, total;
        synchronized (mTaskGroupLock) {
            Integer n;
            n = mTaskGroupProgresses.get(taskGroupName);
            progress = (n == null ? 0 : n) + deltaProgress;
            mTaskGroupProgresses.put(taskGroupName, progress);

            n = mTaskGroupTotals.get(taskGroupName);
            total = (n == null ? 0 : n) + deltaTotal;
            mTaskGroupTotals.put(taskGroupName, total);
        }

        synchronized (mListeners) {
            for (SchedulerEventListener listener : mListeners) {
                listener.taskGroupUpdated(taskGroupName, progress, total);
            }
        }
    }

    @Override
    public List<String> getTaskGroupNames() {
        final List<String> list = new ArrayList<String>();
        list.add(UPDATE_CACHE_GROUP_NAME);
        list.add(DOWNLOAD_DATA_GROUP_NAME);
        list.add(REPROJECT_MODIS_GROUP_NAME);
        list.add(REPROJECT_TRMM_GROUP_NAME);
        list.add(REPROJECT_ETO_GROUP_NAME);
        list.add(CALCULATE_INDICES_GROUP_NAME);
        list.add(CALCULATE_ZONAL_STATS_GROUP_NAME);
        list.add(UPLOAD_RESULTS_GROUP_NAME);
        return Collections.unmodifiableList(list);
    }

    @Override
    public void removeSchedulerEventListener(SchedulerEventListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void newTask(Task task) {
        onNewTask(task.getName(), false);
    }

    @Override
    public void taskCompleted(Task task) {
        onTaskCompleted(task.getName());
    }

    @Override
    public void taskFailed(Task task, Throwable cause) {
        ErrorLog.add("Failed task: " + task.getName(), cause);
        onTaskFailed(task.getName(), cause);
    }

    /**
     * Registers callbacks to trigger MODIS reprojection and ETa calculation
     */
    private void setupSubscriptions() {
        // When sets of MODIS tiles finish downloading, they should start reprojection tasks
        for (ProjectInfo project : mProjects) {
            final Set<ModisTile> tiles = new HashSet<ModisTile>();
            Collections.addAll(tiles, project.getModisTiles());

            mModisReprojectionDependencyTracker.subscribe(tiles, new ModisDownloadsConvergedAction(project));
        }

        // Register callbacks to trigger ETa calculation
        for (ProjectInfo project : mProjects) {
            if (project.shouldCalculateETa()) {
                mEtaDependencyTracker.subscribe(project, new EtaConvergedAction(project));
            }
        }
    }

    /**
     * Enqueues the initial download cache tasks that eventually trigger all of the work.
     */
    private void enqueueInitialTasks() {
        incrementTaskGroup(UPDATE_CACHE_GROUP_NAME, 0, 5);

        mModisDownloadQueue.enqueueCheckDateCache(ModisProduct.NBAR, mOldestStartDate,
                new CheckModisDateCacheContinuation(ModisProduct.NBAR));

        mModisDownloadQueue.enqueueCheckDateCache(ModisProduct.LST, mOldestStartDate,
                new CheckModisDateCacheContinuation(ModisProduct.LST));

        mTrmmDownloadQueue.enqueueCheckDateCache(TrmmProduct.TRMM_3B42, mOldestStartDate,
                new CheckTrmmDateCacheContinuation(TrmmProduct.TRMM_3B42));

        mTrmmDownloadQueue.enqueueCheckDateCache(TrmmProduct.TRMM_3B42RT, mOldestStartDate,
                new CheckTrmmDateCacheContinuation(TrmmProduct.TRMM_3B42RT));

        mEtoDownloadQueue.enqueueCheckArchiveCache(mOldestStartDate,
                new CheckEtoArchiveCacheContinuation());
    }

    /**
     * Continues "Check MODIS date cache" tasks with "Check MODIS tile cache" tasks.
     */
    private final class CheckModisDateCacheContinuation implements Action<DateCache> {
        private final ModisProduct mProduct;

        public CheckModisDateCacheContinuation(ModisProduct product) {
            mProduct = product;
        }

        @Override
        public void act(DateCache cache) throws Exception {
            incrementTaskGroup(UPDATE_CACHE_GROUP_NAME, 1, cache.getDates().size());

            for (final DataDate date : cache.getDates()) {
                mModisDownloadQueue.enqueueCheckTileCache(mProduct, date,
                        new CheckModisTileCacheContinuation(mProduct, date));
            }
        }
    }

    /**
     * Continues "Check TRMM date cache" tasks with TRMM downloads.
     */
    private final class CheckTrmmDateCacheContinuation implements Action<DateCache> {

        private final TrmmProduct mProduct;

        public CheckTrmmDateCacheContinuation(TrmmProduct product) {
            mProduct = product;
        }

        @Override
        public void act(DateCache cache) throws Exception {
            incrementTaskGroup(UPDATE_CACHE_GROUP_NAME, 1, 0);
            incrementTaskGroup(DOWNLOAD_DATA_GROUP_NAME, 0, cache.getDates().size());

            for (DataDate date : cache.getDates()) {
                mTrmmDownloadQueue.enqueueDownload(mProduct, date, new TrmmDownloadContinuation(mProduct, date));
            }
        }
    }

    /**
     * Continues "Check ETo archive cache" tasks with ETo downloads.
     */
    private final class CheckEtoArchiveCacheContinuation implements Action<EtoArchiveCache> {
        public CheckEtoArchiveCacheContinuation() {
        }

        @Override
        public void act(EtoArchiveCache cache) throws Exception {
            incrementTaskGroup(UPDATE_CACHE_GROUP_NAME, 1, 0);
            incrementTaskGroup(DOWNLOAD_DATA_GROUP_NAME, 0, cache.getArchives().size());

            for (EtoArchive archive : cache.getArchives()) {
                System.out.println(archive.toString());
                mEtoDownloadQueue.enqueueDownload(archive, new EtoDownloadContinuation());
            }
        }
    }

    /**
     * Continues TRMM download tasks with TRMM reprojection.
     */
    private final class TrmmDownloadContinuation implements Runnable {
        private final TrmmProduct mProduct;
        private final DataDate mDate;

        public TrmmDownloadContinuation(TrmmProduct product, DataDate date) {
            mProduct = product;
            mDate = date;
        }

        @Override
        public void run() {
            incrementTaskGroup(DOWNLOAD_DATA_GROUP_NAME, 1, 0);
            incrementTaskGroup(REPROJECT_TRMM_GROUP_NAME, 0, mProjects.size());

            for (ProjectInfo project : mProjects) {
                mProcessingQueue.enqueueReprojectTrmm(project, mProduct, mDate,
                        new TrmmReprojectContinuation(project, mProduct, mDate));
            }
        }
    }

    /**
     * Continues ETo download tasks by notifying the ETa dependency tracker.
     */
    private final class EtoDownloadContinuation implements Action<List<DataDate>> {
        public EtoDownloadContinuation() {
        }

        @Override
        public void act(List<DataDate> dates) {
            incrementTaskGroup(DOWNLOAD_DATA_GROUP_NAME, 1, 0);

            for (DataDate date : dates) {
                mEtaDependencyTracker.setEtoDownloaded(date);
            }
        }
    }

    /**
     * Continues "Check MODIS tile cache" tasks with MODIS downloads.
     */
    private final class CheckModisTileCacheContinuation implements Action<ModisTileCache> {
        private final ModisProduct mProduct;
        private final DataDate mDate;

        public CheckModisTileCacheContinuation(ModisProduct product, DataDate date) {
            mProduct = product;
            mDate = date;
        }

        @Override
        public void act(ModisTileCache cache) throws Exception {
            final Set<ModisTile> tiles = new HashSet<ModisTile>(mModisTileUnion);
            tiles.retainAll(cache.getTiles().keySet());

            incrementTaskGroup(UPDATE_CACHE_GROUP_NAME, 1, 0);
            incrementTaskGroup(DOWNLOAD_DATA_GROUP_NAME, 0, tiles.size());

            for (ModisTile tile : tiles) {
                final DataDate processed = cache.getTiles().get(tile);

                final ModisId modisId = new ModisId(mProduct, mDate, tile, processed);
                mModisDownloadQueue.enqueueDownload(modisId,
                        new ModisDownloadContinuation(modisId));
            }
        }
    }

    /**
     * Continues MODIS download tasks by notifying the MODIS reprojection dependency tracker.
     */
    private final class ModisDownloadContinuation implements Runnable {
        private final ModisId mModisId;

        public ModisDownloadContinuation(ModisId modisId) {
            mModisId = modisId;
        }

        @Override
        public void run() {
            incrementTaskGroup(DOWNLOAD_DATA_GROUP_NAME, 1, 0);

            mModisReprojectionDependencyTracker.add(mModisId);
        }
    }

    /**
     * Starts MODIS reprojection when dependencies are ready.
     */
    private final class ModisDownloadsConvergedAction implements Action2<ModisProduct, DataDate> {
        private final ProjectInfo mProject;

        public ModisDownloadsConvergedAction(ProjectInfo project) {
            mProject = project;
        }

        @Override
        public void act(final ModisProduct product, final DataDate date) {
            incrementTaskGroup(REPROJECT_MODIS_GROUP_NAME, 0, 1);

            mProcessingQueue.enqueuePrepareModis(mProject, product, date,
                    new ModisReprojectContinuation(mProject, product, date));
        }
    }

    /**
     * Starts ETo reprojection when dependencies are ready.
     */
    private final class EtaConvergedAction implements Action<DataDate> {
        private final ProjectInfo mProject;

        public EtaConvergedAction(ProjectInfo project) {
            mProject = project;
        }

        @Override
        public void act(DataDate date) throws Exception {
            incrementTaskGroup(REPROJECT_ETO_GROUP_NAME, 0, 1);

            mProcessingQueue.enqueueReprojectEto(mProject, date,
                    new EtoReprojectContinuation(mProject, date));
        }
    }

    /**
     * Continues MODIS reprojection tasks with MODIS-only index calculation and by notifying the ETa
     * dependency tracker.
     */
    private final class ModisReprojectContinuation implements Runnable {
        private final ProjectInfo mProject;
        private final ModisProduct mProduct;
        private final DataDate mDate;

        public ModisReprojectContinuation(ProjectInfo project, ModisProduct product,
                DataDate date) {
            mProject = project;
            mProduct = product;
            mDate = date;
        }

        @Override
        public void run() {
            incrementTaskGroup(REPROJECT_MODIS_GROUP_NAME, 1, 0);

            for (String feature : mProject.getShapeFiles()) {
                mProcessingQueue.enqueueModisClip(
                        mProject, mProduct, mDate, feature,
                        new ModisClipContinuation(mProject, mProduct, mDate, feature)
                );
            }

            mEtaDependencyTracker.setModisCompleted(mProject, mProduct, mDate);
        }
    }

    private final class ModisClipContinuation implements Runnable {

        private final ProjectInfo mProject;
        private final ModisProduct mProduct;
        private final DataDate mDate;
        private final String mFeature;

        public ModisClipContinuation(ProjectInfo project, ModisProduct product,
                DataDate date, String feature) {
            mProject = project;
            mProduct = product;
            mDate = date;
            mFeature = feature;
        }

        @Override
        public void run() {
            if (mProduct.equals(ModisProduct.NBAR)) {
                incrementTaskGroup(CALCULATE_INDICES_GROUP_NAME, 0, 5);

                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.NDVI, mDate, mFeature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.NDVI));
                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.NDWI5, mDate, mFeature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.NDWI5));
                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.NDWI6, mDate, mFeature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.NDWI6));
                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.EVI, mDate, mFeature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.EVI));
                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.SAVI, mDate, mFeature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.SAVI));
            } else {
                incrementTaskGroup(CALCULATE_INDICES_GROUP_NAME, 0, 3);

                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.LST_DAY, mDate, mFeature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.LST_DAY));
                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.LST_NIGHT, mDate, mFeature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.LST_NIGHT));
                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.LST_MEAN, mDate, mFeature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.LST_MEAN));
            }
        }

    }

    //    /**
    //     * Continues MODIS reprojection tasks with MODIS-only index calculation and by notifying the ETa
    //     * dependency tracker.
    //     */
    //    private final class ModisReprojectContinuation implements Runnable {
    //        private final ProjectInfo mProject;
    //        private final ModisProduct mProduct;
    //        private final DataDate mDate;
    //
    //        public ModisReprojectContinuation(ProjectInfo project, ModisProduct product,
    //                DataDate date) {
    //            mProject = project;
    //            mProduct = product;
    //            mDate = date;
    //        }
    //
    //        /**
    //         * Returns a map from indices to calculate to indices that need zonal statistics.
    //         * (because LST calculates its indices all at once, but needs three separate stats runs)
    //         */
    //        private Map<EnvironmentalIndex, EnvironmentalIndex[]> getIndices() {
    //            final Map<EnvironmentalIndex, EnvironmentalIndex[]> result =
    //                    new HashMap<EnvironmentalIndex, EnvironmentalIndex[]>();
    //                    switch (mProduct) {
    //                    case NBAR:
    //                        result.put(EnvironmentalIndex.NDVI, new EnvironmentalIndex[] { EnvironmentalIndex.NDVI });
    //                        result.put(EnvironmentalIndex.EVI, new EnvironmentalIndex[] { EnvironmentalIndex.EVI });
    //                        result.put(EnvironmentalIndex.NDWI5, new EnvironmentalIndex[] { EnvironmentalIndex.NDWI5 });
    //                        result.put(EnvironmentalIndex.NDWI6, new EnvironmentalIndex[] { EnvironmentalIndex.NDWI6 });
    //                        result.put(EnvironmentalIndex.SAVI, new EnvironmentalIndex[] { EnvironmentalIndex.SAVI });
    //                        return result;
    //
    //                    case LST:
    //                        result.put(EnvironmentalIndex.LST_MEAN, new EnvironmentalIndex[] {
    //                                EnvironmentalIndex.LST_DAY,
    //                                EnvironmentalIndex.LST_NIGHT,
    //                                EnvironmentalIndex.LST_MEAN
    //                        });
    //                        return result;
    //
    //                    default:
    //                        throw new IllegalArgumentException();
    //                    }
    //        }
    //
    //        @Override
    //        public void run() {
    //            final Map<EnvironmentalIndex, EnvironmentalIndex[]> indices = getIndices();
    //
    //            incrementTaskGroup(REPROJECT_MODIS_GROUP_NAME, 1, 0);
    //            incrementTaskGroup(CALCULATE_INDICES_GROUP_NAME, 0, indices.size());
    //
    //            for (Map.Entry<EnvironmentalIndex, EnvironmentalIndex[]> entry : indices.entrySet()) {
    //                mProcessingQueue.enqueueCalculateIndex(mProject, entry.getKey(), mDate,
    //                        new CalculateIndexContinuation(mProject, mDate, entry.getValue()));
    //            }
    //
    //            mEtaDependencyTracker.setModisCompleted(mProject, mProduct, mDate);
    //        }
    //    }

    /**
     * Continues TRMM reprojection tasks with TRMM index calculation.
     */
    private final class TrmmReprojectContinuation implements Runnable {
        private final ProjectInfo mProject;
        private final TrmmProduct mProduct;
        private final DataDate mDate;

        public TrmmReprojectContinuation(ProjectInfo project, TrmmProduct product, DataDate date) {
            mProject = project;
            mProduct = product;
            mDate = date;
        }

        @Override
        public void run() {
            incrementTaskGroup(REPROJECT_TRMM_GROUP_NAME, 1, 0);

            for (String feature : mProject.getShapeFiles()) {
                mProcessingQueue.enqueueTrmmClip(mProject, mProduct, mDate, feature,
                        new TrmmClipContinuation(mProject, mProduct, mDate, feature));
            }
        }
    }

    private final class TrmmClipContinuation implements Runnable {
        private final ProjectInfo mProject;
        private final TrmmProduct mProduct;
        private final DataDate mDate;
        private final String mFeature;

        public TrmmClipContinuation(ProjectInfo project, TrmmProduct product, DataDate date,
                String feature) {
            mProject = project;
            mProduct = product;
            mDate = date;
            mFeature = feature;
        }

        @Override
        public void run() {
            incrementTaskGroup(CALCULATE_INDICES_GROUP_NAME, 0, 1);

            EnvironmentalIndex index = mProduct.equals(TrmmProduct.TRMM_3B42) ? EnvironmentalIndex.TRMM : EnvironmentalIndex.TRMM_RT;
            mProcessingQueue.enqueueCalculateIndex(mProject, index, mDate, mFeature,
                    new CalculateIndexContinuation(mProject, mDate, index));
        }

    }

    /**
     * Continues ETo reprojection with ETa calculation.
     */
    private final class EtoReprojectContinuation implements Runnable {
        private final ProjectInfo mProject;
        private final DataDate mDate;

        public EtoReprojectContinuation(ProjectInfo project, DataDate date) {
            mProject = project;
            mDate = date;
        }

        @Override
        public void run() {
            incrementTaskGroup(REPROJECT_ETO_GROUP_NAME, 1, 0);
            incrementTaskGroup(CALCULATE_INDICES_GROUP_NAME, 0, 1);

            for (String feature : mProject.getShapeFiles()) {
                mProcessingQueue.enqueueCalculateIndex(mProject, EnvironmentalIndex.ETA, mDate, feature,
                        new CalculateIndexContinuation(mProject, mDate, EnvironmentalIndex.ETA));
            }
        }
    }

    /**
     * Continues index calculation with zonal statistics calculation.
     */
    private final class CalculateIndexContinuation implements Runnable {
        private final ProjectInfo mProject;
        private final List<EnvironmentalIndex> mIndices;
        private final DataDate mDate;

        public CalculateIndexContinuation(ProjectInfo project, DataDate date,
                EnvironmentalIndex... indices) { // FIXME: no reason to have indices as a list!
            mProject = project;
            mIndices = Collections.unmodifiableList(new ArrayList<EnvironmentalIndex>(
                    Arrays.asList(indices)));
            mDate = date;
        }

        @Override
        public void run() {
            incrementTaskGroup(CALCULATE_INDICES_GROUP_NAME, 1, 0);
            incrementTaskGroup(CALCULATE_ZONAL_STATS_GROUP_NAME, 0, mIndices.size());

            for (EnvironmentalIndex index : mIndices) {
                mProcessingQueue.enqueueCalculateZonalStatistics(mProject, index, mDate,
                        new CalculateZonalStatisticsContinuation(mProject, index, mDate));
            }
        }
    }

    /**
     * Continues zonal statistics calculation with result uploads.
     */
    private final class CalculateZonalStatisticsContinuation implements Runnable {
        private final ProjectInfo mProject;
        private final EnvironmentalIndex mIndex;
        private final DataDate mDate;

        public CalculateZonalStatisticsContinuation(ProjectInfo project, EnvironmentalIndex index,
                DataDate date) {
            mProject = project;
            mIndex = index;
            mDate = date;
        }

        @Override
        public void run() {
            incrementTaskGroup(CALCULATE_ZONAL_STATS_GROUP_NAME, 1, 0);
            incrementTaskGroup(UPLOAD_RESULTS_GROUP_NAME, 0, 1);

            mProcessingQueue.enqueueUploadResults(mProject, mIndex, mDate,
                    new UploadResultsContinuation());
        }
    }

    /**
     * Continues result uploads by reporting completion.
     */
    private final class UploadResultsContinuation implements Runnable {
        @Override
        public void run() {
            incrementTaskGroup(UPLOAD_RESULTS_GROUP_NAME, 1, 0);
        }
    }

}