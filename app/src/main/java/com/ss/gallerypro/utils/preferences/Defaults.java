package com.ss.gallerypro.utils.preferences;

import com.ss.gallerypro.data.LayoutType;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;

public class Defaults {

    // Prevent class instantiation
    private Defaults() {}
    public static final int ALBUM_SORTING_MODE = SortingMode.DATE.getValue();
    public static final int ALBUM_SORTING_ORDER = SortingOrder.DESCENDING.getValue();
    public static final int ALBUM_LAYOUT_TYPE = LayoutType.GRID.getValue();

    public static final int RUN_SPLASH_DEFAULT_VALUE = 0;
}
