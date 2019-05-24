package com.ss.gallerypro.data.filter;

import java.util.stream.Stream;

public enum AlbumFilter {

    IMAGE(1), VIDEO(0);

    int value;

    AlbumFilter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AlbumFilter fromValue(int value) {
        switch (value) {
            case 0:
                return IMAGE;
            case 1:
                return VIDEO;
        }
        return null;
    }

    public static String[] getNames() {
        return Stream.of(AlbumFilter.values()).map(AlbumFilter::name).toArray(String[]::new);
    }
}
