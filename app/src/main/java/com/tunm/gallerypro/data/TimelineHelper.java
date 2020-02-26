package com.tunm.gallerypro.data;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.utils.preferences.Prefs;

public class TimelineHelper {
    @NonNull
    public static SortingMode getSortingMode() {
        return Prefs.getTimelineSortingMode();
    }

    @NonNull
    public static SortingOrder getSortingOrder() {
        return Prefs.getTimelineSortingOrder();
    }

    public static void setSortingMode(@NonNull SortingMode sortingMode) {
        Prefs.setTimelineSortingMode(sortingMode);
    }

    public static void setSortingOrder(@NonNull SortingOrder sortingOrder) {
        Prefs.setTimelineSortingOrder(sortingOrder);
    }

    public static int getNumbColumnPort(Activity activity) {
        return Prefs.getAlbumColumnPort(activity);
    }

    public static int getNumbColumnLand(Activity activity) {
        return Prefs.getAlbumColumnLand(activity);
    }

    public static void setNumbColumnPort(int numb) {
        Prefs.setAlbumColumnPort(numb);
    }

    public static void setNumbColumnLand(int numb) {
        Prefs.setAlbumColumnLand(numb);
    }
}
