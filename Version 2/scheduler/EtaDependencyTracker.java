package edu.sdstate.eastweb.prototype.scheduler;

import java.util.*;

import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.scheduler.framework.Action;

/**
 * Tracks dependencies for ETa calculation, triggering callbacks when ready.
 */
public class EtaDependencyTracker {
    private static final class Entry {
        private boolean mIsNbarReprojected = false;
        private boolean mIsLstReprojected = false;
        private int mNumEtoDownloaded = 0;

        public Entry() {
        }

        public void setNbarReprojected() {
            mIsNbarReprojected = true;
        }

        public void setLstReprojected() {
            mIsLstReprojected = true;
        }

        public void countEtoDownloaded() {
            ++mNumEtoDownloaded;
        }

        public boolean getIsReady() {
            return mIsNbarReprojected && mIsLstReprojected && (mNumEtoDownloaded >= 8);
        }
    }

    private static final class Subscription {
        private final Action<DataDate> mCallback;
        private final Map<DataDate, Entry> mEntries = new HashMap<DataDate, Entry>();
        private final Set<DataDate> mUnknownDownloadedEtoDates = new HashSet<DataDate>();

        public Subscription(Action<DataDate> callback) {
            mCallback = callback;
        }

        private Entry getEntry(DataDate date) {
            final Entry entry;
            if (mEntries.containsKey(date)) {
                entry = mEntries.get(date);
            } else {
                mEntries.put(date, entry = new Entry());

                // Retrieve unknown ETo dates in range
                // Composite dates are the start date and the seven days after it
                for (int i = 0; i < 8; ++i) {
                    final DataDate shiftedDate = date.next(i);
                    if (mUnknownDownloadedEtoDates.contains(shiftedDate)) {
                        mUnknownDownloadedEtoDates.remove(shiftedDate);
                        entry.countEtoDownloaded();
                    }
                }
            }
            return entry;
        }

        private void checkEntry(DataDate date, Entry entry) {
            if (entry.getIsReady()) {
                mEntries.remove(date);

                try {
                    mCallback.act(date);
                } catch (Throwable e) {
                    System.err.println("EtaDependencyTracker: a callback threw an exception:");
                    e.printStackTrace();
                }
            }
        }

        public void setNbarReprojected(DataDate date) {
            final Entry entry = getEntry(date);
            entry.setNbarReprojected();
            checkEntry(date, entry);
        }

        public void setLstReprojected(DataDate date) {
            final Entry entry = getEntry(date);
            entry.setLstReprojected();
            checkEntry(date, entry);
        }

        public void setEtoDownloaded(DataDate date) {
            // Find an existing entry whose composite would include this date
            // The entry's date would be this date or any of the seven dates before it
            for (int i = 0; i < 8; ++i) {
                final DataDate shiftedDate = date.next(-i);
                final Entry entry = mEntries.get(shiftedDate);
                if (entry != null) {
                    entry.countEtoDownloaded();
                    checkEntry(shiftedDate, entry);
                    return;
                }
            }

            // No result -- add it to a list to be counted later in getEntry()
            mUnknownDownloadedEtoDates.add(date);
        }
    }

    private final Map<ProjectInfo, Subscription> mSubscriptions =
        new HashMap<ProjectInfo, Subscription>();

    public void subscribe(ProjectInfo project, Action<DataDate> callback) {
        synchronized (mSubscriptions) {
            mSubscriptions.put(project, new Subscription(callback));
        }
    }

    public void setNbarCompleted(ProjectInfo project, DataDate date) {
        synchronized (mSubscriptions) {
            final Subscription sub = mSubscriptions.get(project);
            if (sub != null) {
                sub.setNbarReprojected(date);
            }
        }
    }

    public void setLstCompleted(ProjectInfo project, DataDate date) {
        synchronized (mSubscriptions) {
            final Subscription sub = mSubscriptions.get(project);
            if (sub != null) {
                sub.setLstReprojected(date);
            }
        }
    }

    public void setModisCompleted(ProjectInfo project, ModisProduct product, DataDate date) {
        switch (product) {
        case NBAR:
            setNbarCompleted(project, date);
            break;

        case LST:
            setLstCompleted(project, date);
            break;
        }
    }

    public void setEtoDownloaded(DataDate date) {
        synchronized (mSubscriptions) {
            for (Subscription sub : mSubscriptions.values()) {
                sub.setEtoDownloaded(date);
            }
        }
    }
}