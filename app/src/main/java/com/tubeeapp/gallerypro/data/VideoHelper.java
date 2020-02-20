package com.tubeeapp.gallerypro.data;

import android.support.annotation.NonNull;

import com.tubeeapp.gallerypro.data.sort.SortingMode;
import com.tubeeapp.gallerypro.data.sort.SortingOrder;
import com.tubeeapp.gallerypro.utils.preferences.Prefs;

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
