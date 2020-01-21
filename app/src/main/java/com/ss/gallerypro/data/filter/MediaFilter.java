package com.ss.gallerypro.data.filter;

import java.util.LinkedList;

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
        LinkedList<String> list = new LinkedList<>();
        for (MediaFilter s : MediaFilter.values()) {
            list.add(s.name());
        }

        return list.toArray(new String[list.size()]);
    }
}
