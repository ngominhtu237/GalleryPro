package com.ss.gallerypro.data.filter;

import java.util.stream.Stream;

public enum MediaFilter {

    IMAGE(1), VIDEO(0);

    int value;

    MediaFilter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MediaFilter fromValue(int value) {
        switch (value) {
            case 0:
                return IMAGE;
            case 1:
                return VIDEO;
        }
        return null;
    }

    public static String[] getNames() {
        return Stream.of(MediaFilter.values()).map(MediaFilter::name).toArray(String[]::new);
    }
}
