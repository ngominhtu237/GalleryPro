package com.tunm.gallerypro.data;

import android.app.Activity;
import android.content.Context;

import com.tunm.gallerypro.utils.preferences.Prefs;

import java.io.File;
import java.util.ArrayList;

public class MediaHelper {
    private static final String TAG = "MediaHelper";

    public static boolean deleteMedia(Context context, String path) {
        return StorageHelper.deleteMedia(context, new File(path));
    }

    public static Long getSizeMedia(ArrayList<MediaItem> mediaItems){
        Long s = 0L;
        for(int i=0; i<mediaItems.size(); i++){
            s+= Long.valueOf(mediaItems.get(i).getSize());
        }
        return s;
    }

    public static int getTimelineColumnPortrait(Activity activity) {
        return Prefs.getTimelineColumnPortrait(activity);
    }

    public static void setTimelineColumnPortrait(int columnNumber) {
        Prefs.setTimelineColumnPortrait(columnNumber);
    }

    public static int getTimelineColumnLandscape(Activity activity) {
        return Prefs.getTimelineColumnLandscape(activity);
    }

    public static void setTimelineColumnLandscape(int columnNumber) {
        Prefs.setTimelineColumnLandscape(columnNumber);
    }
}
