package com.ss.gallerypro.data;

import android.support.annotation.NonNull;

import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.utils.preferences.Prefs;

public class VideoHelper {

    @NonNull
    public static SortingMode getSortingMode() {
        return Prefs.getVideoSortingMode();
    }

    @NonNull
    public static SortingOrder getSortingOrder() {
        return Prefs.getVideoSortingOrder();
    }

    public static void setSortingMode(@NonNull SortingMode sortingMode) {
        Prefs.setVideoSortingMode(sortingMode);
    }

    public static void setSortingOrder(@NonNull SortingOrder sortingOrder) {
        Prefs.setVideoSortingOrder(sortingOrder);
    }
}
