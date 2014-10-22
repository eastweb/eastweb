package edu.sdstate.eastweb.prototype.scheduler;

import java.util.*;
import java.util.concurrent.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.*;

abstract class BaseTaskQueue {
    private enum EntryType {
        ACTUAL_TASK,
        SKIP_CHECK
    }

    private static final int TERMINATE_PRIORITY = -1; // Highest priority
    private static final int MAX_FAILURES = 2;

    protected abstract class QueueEntry implements Comparable<QueueEntry> {
        protected final EntryType mEntryType;
        protected final int mPriority;

        public QueueEntry(EntryType entryType, int priority) {
            mEntryType = entryType;
            mPriority = priority;
        }

        public abstract boolean getShouldTerminate();

        public abstract void run();

        @Override
        public int compareTo(QueueEntry o) {
            // Sort by entry type -- skip checks come first, followed by actual tasks
            int cmp = mEntryType.ordinal() - o.mEntryType.ordinal();
            if (cmp != 0) {
                return cmp;
            }

            return mPriority - o.mPriority;
        }
    }

    protected final class TerminateQueueEntry extends QueueEntry {
        public TerminateQueueEntry() {
            super(EntryType.SKIP_CHECK, TERMINATE_PRIORITY);
        }

        @Override
        public boolean getShouldTerminate() {
            return true;
        }

        @Override
        public void run() {
        }
    }

    protected final class CallableTaskQueueEntry<T> extends QueueEntry {
        private final CallableTask<T> mCallableTask;
        private final Action<T> mContinuation;
        private final int mFailures;

        private CallableTaskQueueEntry(EntryType entryType, int priority,
                CallableTask<T> callableTask, Action<T> continuation, int failures) {
            super(entryType, priority);
            mCallableTask = callableTask;
            mContinuation = continuation;
            mFailures = failures;
        }

        public CallableTaskQueueEntry(int priority, CallableTask<T> callableTask,
                Action<T> continuation) {
            this(EntryType.SKIP_CHECK, priority, callableTask, continuation, 0);
        }

        @Override
        public boolean getShouldTerminate() {
            return false;
        }

        @Override
        public void run() {
            switch (mEntryType) {
            case SKIP_CHECK:
                runSkipCheck();
                break;

            case ACTUAL_TASK:
                //modified by jiameng. add try block to catch the null pointer error caused by trmm connection error
                try{
                    runActualTask();
                }catch(NullPointerException e){
                    break;
                }

                break;
            }
        }

        private void runSkipCheck() {
            final T canSkip;
            try {
                canSkip = mCallableTask.getCanSkip();
            } catch (Throwable e) {
                ErrorLog.add("BaseTaskQueue: a skip check threw an exception", e);
                return;
            }

            if (canSkip != null) {
                // TODO: Remove debug prints
                System.out.println("Skipping task: " + mCallableTask.getName());

                // Task is skippable -- call the continuation
                if (mContinuation != null) {
                    try {
                        mContinuation.act(canSkip);
                    } catch (Throwable e) {
                        ErrorLog.add("BaseTaskQueue: a task continuation thew an exception", e);
                    }
                }
            } else {
                // TODO: Remove debug prints
                System.out.println("Re-enqueing actual task: " + mCallableTask.getName());

                // Task is not skippable -- re-enqueue to actually run
                enqueue(new CallableTaskQueueEntry<T>(
                        EntryType.ACTUAL_TASK, mPriority, mCallableTask, mContinuation, 0));
            }
        }

        private void runActualTask() {
            // TODO: Remove debug prints
            System.out.println("Running task: " + mCallableTask.getName());

            onNewTask(mCallableTask);

            final T result;
            try {
                result = mCallableTask.call();
            } catch (Throwable e) {
                onTaskFailed(mCallableTask, e);

                if (mFailures < MAX_FAILURES) {
                    // Retry once automatically
                    enqueue(new CallableTaskQueueEntry<T>(
                            EntryType.ACTUAL_TASK, mPriority, mCallableTask, mContinuation, mFailures + 1));
                }
                return;
            }

            onTaskCompleted(mCallableTask);

            if (mContinuation != null) {
                try {
                    mContinuation.act(result);
                } catch (Throwable e) {
                    ErrorLog.add("BaseTaskQueue: a task continuation threw an exception", e);
                }
            }
        }
    }

    protected final class RunnableTaskQueueEntry extends QueueEntry {
        private final RunnableTask mRunnableTask;
        private final Runnable mContinuation;
        private final int mFailures;

        private RunnableTaskQueueEntry(EntryType entryType, int priority, RunnableTask runnableTask,
                Runnable continuation, int failures) {
            super(entryType, priority);
            mRunnableTask = runnableTask;
            mContinuation = continuation;
            mFailures = failures;
        }

        public RunnableTaskQueueEntry(int priority, RunnableTask runnableTask,
                Runnable continuation) {
            this(EntryType.SKIP_CHECK, priority, runnableTask, continuation, 0);
        }

        @Override
        public boolean getShouldTerminate() {
            return false;
        }

        @Override
        public void run() {
            switch (mEntryType) {
            case SKIP_CHECK:
                runSkipCheck();
                break;

            case ACTUAL_TASK:
                runActualTask();
                break;
            }
        }

        private void runSkipCheck() {
            final boolean canSkip;
            try {
                canSkip = mRunnableTask.getCanSkip();
            } catch (Throwable e) {
                ErrorLog.add("BaseTaskQueue: a skip check threw an exception", e);
                return;
            }

            if (canSkip) {
                // TODO: Remove debug prints
                System.out.println("Skipping task: " + mRunnableTask.getName());

                // Task is skippable -- call the continuation
                if (mContinuation != null) {
                    try {
                        mContinuation.run();
                    } catch (Throwable e) {
                        ErrorLog.add("BaseTaskQueue: a task continuation thew an exception", e);
                    }
                }
            } else {
                // TODO: Remove debug prints
                System.out.println("Re-enqueing actual task: " + mRunnableTask.getName());

                // Task is not skippable -- re-enqueue to actually run
                enqueue(new RunnableTaskQueueEntry(
                        EntryType.ACTUAL_TASK, mPriority, mRunnableTask, mContinuation, 0));
            }
        }

        private void runActualTask() {
            // TODO: Remove debug prints
            System.out.println("Running task: " + mRunnableTask.getName());

            onNewTask(mRunnableTask);

            try {
                mRunnableTask.run();
            } catch (Throwable e) {
                onTaskFailed(mRunnableTask, e);

                if (mFailures < MAX_FAILURES) {
                    // Retry once automatically
                    enqueue(new RunnableTaskQueueEntry(
                            EntryType.ACTUAL_TASK, mPriority, mRunnableTask, mContinuation, mFailures + 1));
                }
                return;
            }

            onTaskCompleted(mRunnableTask);

            if (mContinuation != null) {
                try {
                    mContinuation.run();
                } catch (Throwable e) {
                    ErrorLog.add("BaseTaskQueue: a task continuation threw an exception", e);
                }
            }
        }
    }

    private final class WorkerRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                final QueueEntry entry;
                try {
                    entry = mQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }

                if (entry.getShouldTerminate()) {
                    System.out.println("Worker thread terminating");
                    return;
                }

                entry.run();
            }
        }
    }

    private final SchedulerFeedback mFeedback;
    private boolean mStarted = false;
    private final PriorityBlockingQueue<QueueEntry> mQueue =
        new PriorityBlockingQueue<QueueEntry>();
    private final List<Thread> mThreads = new ArrayList<Thread>();

    public BaseTaskQueue(SchedulerFeedback feedback) {
        mFeedback = feedback;
    }

    protected abstract int getNumThreads();

    protected void enqueue(QueueEntry entry ) {
        mQueue.put(entry);
    }

    public void start() {
        if (!mStarted) {
            for (int i = 0; i < getNumThreads(); ++i) {
                final Thread thread = new Thread(new WorkerRunnable());
                mThreads.add(thread);
                thread.start();
            }
            mStarted = true;
        }
    }

    public void stop() {
        mQueue.clear();
        for (int i = 0; i < mThreads.size(); ++i) {
            mQueue.add(new TerminateQueueEntry());
        }
    }

    public void join() {
        stop();

        for (Thread thread : mThreads) {
            // Do not yield to interruption -- it isn't used in this program
            while (true) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    continue;
                }
                break;
            }
        }
    }

    private void onNewTask(Task task) {
        try {
            mFeedback.newTask(task);
        } catch (Throwable e) {
            ErrorLog.add("BaseTaskQueue: the new task callback threw an exception", e);
        }
    }

    private void onTaskCompleted(Task task) {
        try {
            mFeedback.taskCompleted(task);
        } catch (Throwable e) {
            ErrorLog.add("BaseTaskQueue: the task completed threw an exception", e);
        }
    }

    private void onTaskFailed(Task task, Throwable cause) {
        try {
            mFeedback.taskFailed(task, cause);
        } catch (Throwable e) {
            ErrorLog.add("BaseTaskQueue: the task failed callback threw an exception", e);
        }
    }
}