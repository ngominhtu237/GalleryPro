package com.ss.gallerypro.data;

public enum LayoutType {
    GRID(0), LIST(1);

    private int value;

    public int getValue() {
        return value;
    }

    LayoutType(int value) {
        this.value = value;
    }

    public static LayoutType fromValue(int value) {
        switch (value) {
            case 0:
                return GRID;
            case 1:
                return LIST;
        }
        return null;
    }
}
