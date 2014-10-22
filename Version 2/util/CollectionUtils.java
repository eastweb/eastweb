package edu.sdstate.eastweb.prototype.util;

import java.util.List;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <T extends Comparable<T>> int compareLists(List<? extends T> a, List<? extends T> b) {
        int cmp = Integer.valueOf(a.size()).compareTo(Integer.valueOf(b.size()));
        if (cmp != 0) {
            return cmp;
        }

        for (int i = 0; i < a.size(); ++i) {
            cmp = a.get(i).compareTo(b.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }

        return 0;
    }
}