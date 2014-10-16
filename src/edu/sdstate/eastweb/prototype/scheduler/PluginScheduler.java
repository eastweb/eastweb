package edu.sdstate.eastweb.prototype.scheduler;

import java.util.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.download.cache.*;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;
import edu.sdstate.eastweb.prototype.scheduler.framework.*;

public final class PluginScheduler implements Scheduler, SchedulerFeedback {
    // Task group names (for progress events)
    private static final String UPDATE_CACHE_GROUP_NAME = "Update download caches";
    private static final String DOWNLOAD_DATA_GROUP_NAME = "Download data";
    private static final String REPROJECT_NLDAS_GROUP_NAME = "Reproject Nldas files";
    private static final String CALCULATE_INDICES_GROUP_NAME = "Calculate environmental indices";
    private static final String CALCULATE_ZONAL_STATS_GROUP_NAME = "Calculate zonal statistics";
    private static final String UPLOAD_RESULTS_GROUP_NAME = "Upload results to the database";

    // Task group counters
    private final Object mTaskGroupLock = new Object();
    private final Map<String, Integer> mTaskGroupProgresses = new HashMap<String, Integer>();
    private final Map<String, Integer> mTaskGroupTotals = new HashMap<String, Integer>();

    // Listener list for new/updated/completed task events
    private final List<SchedulerEventListener> mListeners = new ArrayList<SchedulerEventListener>();
    private final ProcessingQueue mProcessingQueue = new ProcessingQueue(this);
    private final NldasDownloadQueue mNldasDownloadQueue=new NldasDownloadQueue(this);

    // Configuration
    private final List<ProjectInfo> mProjects;
    private final DataDate mOldestStartDate;
    //private final Set<ModisTile> mModisTileUnion;

    private boolean mStarted = false;

    public PluginScheduler(ProjectInfo[] projects) {
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
        // final Set<ModisTile> modisTileUnion = new HashSet<ModisTile>();
        for (ProjectInfo project : mProjects) {
            final DataDate startDate = project.getStartDate();
            if (oldestStartDate == null || oldestStartDate.compareTo(startDate) > 0) {
                oldestStartDate = startDate;
            }

            // Collections.addAll(modisTileUnion, project.getModisTiles());
        }
        mOldestStartDate = oldestStartDate;
        // mModisTileUnion = Collections.unmodifiableSet(modisTileUnion);


    }

    @Override
    public void start() {
        if (!mStarted) {
            //mModisDownloadQueue.start();
            //mTrmmDownloadQueue.start();
            //mEtoDownloadQueue.start();
            mNldasDownloadQueue.start();
            mProcessingQueue.start();
            if (mOldestStartDate != null) {
                enqueueInitialTasks();
            }
            mStarted = true;
        }
    }

    @Override
    public void stop() {
        //mModisDownloadQueue.stop();
        mNldasDownloadQueue.stop();
        //mEtoDownloadQueue.stop();
        mProcessingQueue.stop();
    }

    @Override
    public void join() {
        //mModisDownloadQueue.join();
        mNldasDownloadQueue.join();
        //mEtoDownloadQueue.join();
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
        list.add(REPROJECT_NLDAS_GROUP_NAME);
        //list.add(REPROJECT_TRMM_GROUP_NAME);
        // list.add(REPROJECT_ETO_GROUP_NAME);
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
     * Enqueues the initial download cache tasks that eventually trigger all of the work.
     */
    private void enqueueInitialTasks() {
        incrementTaskGroup(UPDATE_CACHE_GROUP_NAME, 0, 5);

        mNldasDownloadQueue.enqueueCheckDateCache(mOldestStartDate,new CheckNldasDateCacheContinuation());

    }

    /**
     * Continues "Check Nldas date cache" task with download Nldas data task
     * **/
    private final class CheckNldasDateCacheContinuation implements Action<DateCache> {



        @Override
        public void act(DateCache cache) throws Exception {
            incrementTaskGroup(UPDATE_CACHE_GROUP_NAME, 1, cache.getDates().size());

            for (final DataDate date : cache.getDates()) {
                mNldasDownloadQueue.enqueueDownload(date, new NldasDownloadContinuation(date));
            }
        }
    }

    /**
     * Continues "download Nldas data" task with project Nldas data task
     * **/

    private final class NldasDownloadContinuation implements Runnable {

        private final DataDate mDate;

        public NldasDownloadContinuation( DataDate date) {
            mDate = date;
        }

        @Override
        public void run() {
            incrementTaskGroup(DOWNLOAD_DATA_GROUP_NAME, 1, 0);
            incrementTaskGroup(REPROJECT_NLDAS_GROUP_NAME, 0, mProjects.size());

            for (ProjectInfo project : mProjects) {
                mProcessingQueue.enqueueReprojectNldas(project, mDate,
                        new NldasReprojectContinuation(project, mDate));
            }
        }
    }

    /**
     * Continues NLDAS reprojection tasks with TRMM index calculation.
     */
    private final class NldasReprojectContinuation implements Runnable {
        private final ProjectInfo mProject;
        private final DataDate mDate;


        public NldasReprojectContinuation(ProjectInfo project,  DataDate date) {
            mProject = project;
            mDate = date;
        }

        @Override
        public void run() {
            incrementTaskGroup(REPROJECT_NLDAS_GROUP_NAME, 1, 0);

            for (String feature : mProject.getShapeFiles()) {
                EnvironmentalIndex index =  EnvironmentalIndex.NLDAS;
                mProcessingQueue.enqueueCalculateIndex(mProject, index, mDate, feature,
                        new CalculateIndexContinuation(mProject, mDate, index));

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