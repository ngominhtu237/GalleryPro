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

    /********** GETTERS **********/
    @NonNull
    public static SortingMode getTimelineSortingMode() {
        return SortingMode.fromValue(getPrefs().get(Keys.TIMELINE_SORTING_MODE, Defaults.TIMELINE_SORTING_MODE));
    }

    @NonNull
    public static SortingOrder getTimelineSortingOrder() {
        return SortingOrder.fromValue(getPrefs().get(Keys.TIMELINE_SORTING_ORDER, Defaults.TIMELINE_SORTING_ORDER));
    }

    @NonNull
    public static SortingMode getVideoSortingMode() {
        return SortingMode.fromValue(getPrefs().get(Keys.VIDEO_SORTING_MODE, Defaults.VIDEO_SORTING_MODE));
    }

    @NonNull
    public static SortingOrder getVideoSortingOrder() {
        return SortingOrder.fromValue(getPrefs().get(Keys.VIDEO_SORTING_ORDER, Defaults.VIDEO_SORTING_ORDER));
    }

    @NonNull
    public static LayoutType getAlbumLayoutType() {
        return LayoutType.fromValue(getPrefs().get(Keys.ALBUM_LAYOUT_TYPE, Defaults.ALBUM_LAYOUT_TYPE));
    }

    public static int getRunSplashScreen() {
        return getPrefs().get(Keys.RUN_SPLASH, Defaults.RUN_SPLASH_DEFAULT_VALUE);
    }

    public static int getTimelineColumnPortrait(Activity activity) {
        return getPrefs().get(Keys.TIMELINE_COLUMN_PORTRAIT, (int) (activity.getResources().getDimension(R.dimen.timeline_column_portrait) / activity.getResources().getDisplayMetrics().density));
    }

    public static int getTimelineColumnLandscape(Activity activity) {
        return getPrefs().get(Keys.TIMELINE_COLUMN_LANDSCAPE, (int) (activity.getResources().getDimension(R.dimen.timeline_column_landscape) / activity.getResources().getDisplayMetrics().density));
    }

    /********** SETTERS **********/
    public static void setAlbumSortingMode(@NonNull SortingMode sortingMode) {
        getPrefs().put(Keys.ALBUM_SORTING_MODE, sortingMode.getValue());
    }

    public static void setAlbumSortingOrder(@NonNull SortingOrder sortingOrder) {
        getPrefs().put(Keys.ALBUM_SORTING_ORDER, sortingOrder.getValue());
    }

    public static void setTimelineSortingMode(@NonNull SortingMode sortingMode) {
        getPrefs().put(Keys.TIMELINE_SORTING_MODE, sortingMode.getValue());
    }

    public static void setTimelineSortingOrder(@NonNull SortingOrder sortingOrder) {
        getPrefs().put(Keys.TIMELINE_SORTING_ORDER, sortingOrder.getValue());
    }

    public static void setVideoSortingMode(@NonNull SortingMode sortingMode) {
        getPrefs().put(Keys.VIDEO_SORTING_MODE, sortingMode.getValue());
    }

    public static void setVideoSortingOrder(@NonNull SortingOrder sortingOrder) {
        getPrefs().put(Keys.VIDEO_SORTING_ORDER, sortingOrder.getValue());
    }

    public static void setTimelineColumnPortrait(int column) {
        getPrefs().put(Keys.TIMELINE_COLUMN_PORTRAIT, column);
    }
    public static void setTimelineColumnLandscape(int column) {
        getPrefs().put(Keys.TIMELINE_COLUMN_LANDSCAPE, column);
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

    public static int getPrimaryColor(Context context) {
        return getPrefs().get(Keys.COLOR_PRIMARY, context.getColor(Defaults.COLOR_PRIMARY));
    }

    public static int getAccentColor(Context context) {
        return getPrefs().get(Keys.COLOR_ACCENT, context.getColor(Defaults.COLOR_ACCENT));
    }

    public static int getHighlightColor(Context context) {
        return getPrefs().get(Keys.COLOR_HIGHLIGHT, context.getColor(Defaults.COLOR_HIGHLIGHT));
    }

    public static int getPrimaryHighlightColor(Context context) {
        return getPrefs().get(Keys.COLOR_PRIMARY_HIGHLIGHT, context.getColor(Defaults.COLOR_PRIMARY_HIGHLIGHT));
    }

    public static int getBackgroundColor(Context context) {
        return getPrefs().get(Keys.COLOR_BACKGROUND, context.getColor(Defaults.COLOR_BACKGROUND));
    }

    public static void setPrimaryColor(int value) {
        getPrefs().put(Keys.COLOR_PRIMARY, value);
    }

    public static void setAccentColor(int value) {
        getPrefs().put(Keys.COLOR_ACCENT, value);
    }

    public static void setHighlightColor(int value) {
        getPrefs().put(Keys.COLOR_HIGHLIGHT, value);
    }

    public static void setPrimaryHighlightColor(int value) {
        getPrefs().put(Keys.COLOR_PRIMARY_HIGHLIGHT, value);
    }

    public static void setBackgroundColor(int value) {
        getPrefs().put(Keys.COLOR_BACKGROUND, value);
    }
}
