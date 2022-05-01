package com.thana.sugarapi.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Designed by: Thana
// Dependencies: SugarAPI
public class Sets {

    @SafeVarargs
    public static <E> List<E> combine(Collection<E>... collections) {
        ArrayList<E> list = new ArrayList<>();
        for (Collection<E> collection : collections) {
            list.addAll(collection);
        }
        return list;
    }

    public static <E> List<E> complement(Collection<E> universe, Collection<E> set) {
        return universe.stream().filter((obj) -> !set.contains(obj)).toList();
    }

    @SafeVarargs
    public static <E> List<E> union(Collection<E>... collections) {
        ArrayList<E> newList = new ArrayList<>();
        List<E> combinedList = combine(collections);
        combinedList.forEach((e) -> {
            if (!newList.contains(e)) {
                newList.add(e);
            }
        });
        return newList;
    }

    public static <E> List<E> intersect(Collection<E> first, Collection<E> second) {
        ArrayList<E> newList = new ArrayList<>();
        first.forEach((e) -> {
            if (second.contains(e)) {
                newList.add(e);
            }
        });
        return newList;
    }
}
