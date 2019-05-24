package com.ss.gallerypro.utils.preferences;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.LayoutType;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;

import java.util.HashSet;
import java.util.Set;

public class Prefs {
    private static SharedPrefs sharedPrefs;

    public static void init(@NonNull Context context) {
        if (sharedPrefs != null) {
            throw new RuntimeException("Prefs has already been instantiated");
        }
        sharedPrefs = new SharedPrefs(context);
    }

    /********** GETTERS **********/
    @NonNull
    public static SortingMode getAlbumSortingMode() {
        return SortingMode.fromValue(
                getPrefs().get(Keys.ALBUM_SORTING_MODE, Defaults.ALBUM_SORTING_MODE));
    }

    @NonNull
    public static SortingOrder getAlbumSortingOrder() {
        return SortingOrder.fromValue(
                getPrefs().get(Keys.ALBUM_SORTING_ORDER, Defaults.ALBUM_SORTING_ORDER));
    }

    @NonNull
    public static LayoutType getAlbumLayoutType() {
        return LayoutType.fromValue(
                getPrefs().get(Keys.ALBUM_LAYOUT_TYPE, Defaults.ALBUM_LAYOUT_TYPE));
    }

    public static int getRunSplashScreen() {
        return getPrefs().get(Keys.RUN_SPLASH, Defaults.RUN_SPLASH_DEFAULT_VALUE);
    }

    /********** SETTERS **********/
    public static void setAlbumSortingMode(@NonNull SortingMode sortingMode) {
        getPrefs().put(Keys.ALBUM_SORTING_MODE, sortingMode.getValue());
    }

    public static void setAlbumSortingOrder(@NonNull SortingOrder sortingOrder) {
        getPrefs().put(Keys.ALBUM_SORTING_ORDER, sortingOrder.getValue());
    }

    public static void setAlbumLayoutType(@NonNull LayoutType layoutType) {
        getPrefs().put(Keys.ALBUM_LAYOUT_TYPE, layoutType.getValue());
    }

    public static void setRunSplashScreen(@NonNull int value) {
        getPrefs().put(Keys.RUN_SPLASH, value);
    }

    @NonNull
    private static SharedPrefs getPrefs() {
        if (sharedPrefs == null) {
            throw new RuntimeException("Prefs has not been instantiated. Call init() with context");
        }
        return sharedPrefs;
    }

    public static int getAlbumColumnPort(Activity activity) {
        return getPrefs().get(Keys.ALBUM_NUMB_COLUMN_PORT, (int) (activity.getResources().getDimension(R.dimen.album_column) / activity.getResources().getDisplayMetrics().density));
    }

    public static int getAlbumColumnLand(Activity activity) {
        return getPrefs().get(Keys.ALBUM_NUMB_COLUMN_LAND, (int) (activity.getResources().getDimension(R.dimen.album_column_landscape) / activity.getResources().getDisplayMetrics().density));
    }

    public static void setAlbumColumnPort(int numb) {
        getPrefs().put(Keys.ALBUM_NUMB_COLUMN_PORT, numb);
    }

    public static void setAlbumColumnLand(int numb) {
        getPrefs().put(Keys.ALBUM_NUMB_COLUMN_LAND, numb);
    }

    public static int getVideoColumnPort(Activity activity) {
        return getPrefs().get(Keys.VIDEO_NUMB_COLUMN_PORT, (int) (activity.getResources().getDimension(R.dimen.video_column) / activity.getResources().getDisplayMetrics().density));
    }

    public static int getVideoColumnLand(Activity activity) {
        return getPrefs().get(Keys.VIDEO_NUMB_COLUMN_LAND, (int) (activity.getResources().getDimension(R.dimen.video_column_landscape) / activity.getResources().getDisplayMetrics().density));
    }

    public static void setVideoColumnPort(int numb) {
        getPrefs().put(Keys.VIDEO_NUMB_COLUMN_PORT, numb);
    }

    public static void setVideoColumnLand(int numb) {
        getPrefs().put(Keys.VIDEO_NUMB_COLUMN_LAND, numb);
    }

    public static void setAlbumFilter(Set<String> listFilter) {
        getPrefs().putSetString(Keys.FILTER_ITEM_CHECK, listFilter);
    }

    public static Set<String> getAlbumFilter(int length) {
        Set<String> state = new HashSet<>();
        for(int i=0; i<length; i++) {
            state.add(String.valueOf(i));
        }
        return getPrefs().getSetString(Keys.FILTER_ITEM_CHECK, state);
    }
}
