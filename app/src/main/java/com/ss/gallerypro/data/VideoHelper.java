package com.ss.gallerypro.data;

import android.app.Activity;

import com.ss.gallerypro.utils.preferences.Prefs;

public class VideoHelper {

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
