package com.tubeeapp.gallerypro.data.sort;

import java.util.LinkedList;

/**
 * Created by Tu on 6/24/2018.
 */

public enum SortingOrder {
    ASCENDING(0), DESCENDING(1);

    int value;

    SortingOrder(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isAscending() {
        return value == ASCENDING.getValue();
    }

    public static SortingOrder fromValue(boolean value) {
        return value ? ASCENDING : DESCENDING;
    }

    public static SortingOrder fromValue(int value) {
        return value == 0 ? ASCENDING : DESCENDING;
    }

    public static String[] getNames() {
        LinkedList<String> list = new LinkedList<>();
        for (SortingOrder s : SortingOrder.values()) {
            list.add(s.name());
        }

        return list.toArray(new String[list.size()]);
    }
}