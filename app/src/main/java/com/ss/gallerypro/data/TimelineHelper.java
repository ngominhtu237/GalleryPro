package com.ss.gallerypro.data;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.utils.preferences.Prefs;

public class TimelineHelper {
    @NonNull
    public static SortingMode getSortingMode() {
        return Prefs.getAlbumSortingMode();
    }

    @NonNull
    public static SortingOrder getSortingOrder() {
        return Prefs.getAlbumSortingOrder();
    }

    public static void setSortingMode(@NonNull SortingMode sortingMode) {
        Prefs.setAlbumSortingMode(sortingMode);
    }

    public static void setSortingOrder(@NonNull SortingOrder sortingOrder) {
        Prefs.setAlbumSortingOrder(sortingOrder);
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
