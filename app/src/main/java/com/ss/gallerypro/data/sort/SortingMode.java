package com.ss.gallerypro.data.sort;

/**
 * Created by Tu on 6/24/2018.
 */

public enum SortingMode {
    NAME (0), DATE_TAKEN(1), SIZE(2), LAST_MODIFIED(3);

    int value;

    SortingMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SortingMode fromValue(int value) {
        switch (value) {
            case 0: return NAME;
            case 1: default: return DATE_TAKEN;
            case 2: return SIZE;
            case 3: return LAST_MODIFIED;
        }
    }
}

