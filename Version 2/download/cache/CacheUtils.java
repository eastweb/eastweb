package edu.sdstate.eastweb.prototype.download.cache;

import edu.sdstate.eastweb.prototype.*;

public final class CacheUtils {
    private CacheUtils() {
    }

    public static boolean isFresh(Cache cache) throws ConfigReadException {
        final DataDate lastUpdated = cache.getLastUpdated();
        final DataDate today = DataDate.today();
        final int expirationDays = Config.getInstance().getDownloadRefreshDays();

        if (lastUpdated.compareTo(today) > 0) {
            return false; // A last-updated date in the future is considered stale
        } else {
            return lastUpdated.next(expirationDays).compareTo(today) > 0;
        }
    }
}