package com.ss.gallerypro.data.sort;

import java.util.stream.Stream;

/**
 * Created by Tu on 6/24/2018.
 */

public enum SortingMode {
    NAME(0), SIZE(1), DATE_TAKEN(2), LAST_MODIFIED(3);

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
            case 1: return SIZE;
            case 2: default: return DATE_TAKEN;
            case 3: return LAST_MODIFIED;
        }
    }

    public static String[] getNames() {
        return Stream.of(SortingMode.values()).map(SortingMode::name).toArray(String[]::new);
    }
}

