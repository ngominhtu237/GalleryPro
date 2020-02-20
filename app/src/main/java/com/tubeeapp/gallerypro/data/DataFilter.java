package com.tubeeapp.gallerypro.data;

public enum DataFilter {

    FILTER_IMAGE(0), FILTER_VIDEO(1);

    private int value;

    public int getValue() {
        return value;
    }

    DataFilter(int value) {
        this.value = value;
    }

    public static DataFilter fromValue(int value) {
        switch (value) {
            case 0:
                return FILTER_IMAGE;
            case 1:
                return FILTER_VIDEO;
        }
        return null;
    }
}
