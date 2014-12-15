package version2.prototype.download.cache;

import version2.prototype.*;

public final class CacheUtils {
    private CacheUtils() {
    }

    public static boolean isFresh(Cache cache) throws ConfigReadException {
        final DataDate lastUpdated = cache.getLastUpdated();
        final DataDate today = DataDate.today();
        final int expirationDays = Config.getInstance().getDownloadRefreshDays();

        return lastUpdated.compareTo(today) > 0 ? false : lastUpdated.next(expirationDays).compareTo(today) > 0;
    }
}