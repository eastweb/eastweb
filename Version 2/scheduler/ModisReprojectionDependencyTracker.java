package edu.sdstate.eastweb.prototype.scheduler;

import java.util.*;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.*;

/**
 * Notifies subscribers whenever a specified set of MODIS tiles is downloaded for any date.
 * 
 * @author Michael VanBemmel
 */
public final class ModisReprojectionDependencyTracker {
    private static final class Subscription {
        private final Set<ModisTile> mTiles;
        private final Action2<ModisProduct, DataDate> mCallback;

        public Subscription(Set<ModisTile> tiles, Action2<ModisProduct, DataDate> callback) {
            mTiles = Collections.unmodifiableSet(new HashSet<ModisTile>(tiles));
            mCallback = callback;
        }

        public Set<ModisTile> getTiles() {
            return mTiles;
        }

        public void callback(ModisProduct product, DataDate date) {
            try {
                mCallback.act(product, date);
            } catch (Throwable e) {
                System.err.println("CompletedModisDownloads: callback threw an exception:");
                e.printStackTrace();
            }
        }
    }

    private final Object mLock = new Object();
    private final Map<DataDate, Set<ModisTile>> mNbarTilesByDate =
        new HashMap<DataDate, Set<ModisTile>>();
    private final Map<DataDate, Set<ModisTile>> mLstTilesByDate =
        new HashMap<DataDate, Set<ModisTile>>();

    private final List<Subscription> mSubscriptions = new ArrayList<Subscription>();

    public ModisReprojectionDependencyTracker() {
    }

    private Map<DataDate, Set<ModisTile>> getMap(ModisProduct product) {
        switch (product) {
        case NBAR:
            return mNbarTilesByDate;

        case LST:
            return mLstTilesByDate;

        default:
            throw new IllegalArgumentException();
        }
    }

    public void subscribe(Set<ModisTile> tiles, Action2<ModisProduct, DataDate> callback) {
        synchronized (mLock) {
            mSubscriptions.add(new Subscription(tiles, callback));
        }
    }

    public void add(ModisId modisId) {
        synchronized (mLock) {
            // Get the set for this (product, date) pair
            final Map<DataDate, Set<ModisTile>> map = getMap(modisId.getProduct());
            final Set<ModisTile> tiles;
            if (map.containsKey(modisId.getDate())) {
                tiles = map.get(modisId.getDate());
            } else {
                map.put(modisId.getDate(), tiles = new HashSet<ModisTile>());
            }

            // Check all subscriptions
            for (Subscription sub : mSubscriptions) {
                // Build a temporary set containing this date's pending tiles for this subscription
                final Set<ModisTile> tmp = new HashSet<ModisTile>(sub.getTiles());
                tmp.removeAll(tiles);

                // If this tile is the last one pending, call the callback
                if (tmp.size() == 1 && tmp.contains(modisId.getTile())) {
                    sub.callback(modisId.getProduct(), modisId.getDate());
                }
            }

            tiles.add(modisId.getTile());
        }
    }
}