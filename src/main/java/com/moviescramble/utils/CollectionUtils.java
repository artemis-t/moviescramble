package com.moviescramble.utils;

import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

    public static <T> Set<T> addEntryToSet(T entry, Set<T> set) {
        if (set == null) {
            set = new HashSet<>();
        }

        if (!set.add(entry)) {
            set.remove(entry);
            set.add(entry);
        }

        return set;
    }
}
