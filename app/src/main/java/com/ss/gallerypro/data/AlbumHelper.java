package com.ss.gallerypro.data;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.utils.preferences.Prefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class AlbumHelper {

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

    public static void setFilter(Set<String> arrPosition) {
        Prefs.setAlbumFilter(arrPosition);
    }

    public static Set<String> getFilter(int lengthArr) {
        return Prefs.getAlbumFilter(lengthArr);
    }

    @NonNull
    public static LayoutType getLayoutType() {
        return Prefs.getAlbumLayoutType();
    }

    public static void setLayoutType(@NonNull LayoutType layoutType) {
        Prefs.setAlbumLayoutType(layoutType);
    }

    public static boolean deleteAlbum(Context context, String path) {
        return StorageHelper.deleteAlbum(context, new File(path));
    }

    public static Long getSizeAlbum(ArrayList<MediaItem> mImageList){
        Long s = 0L;
        for(int i=0; i<mImageList.size(); i++){
            s+= Long.valueOf(mImageList.get(i).getSize());
        }
        return s;
    }
}
