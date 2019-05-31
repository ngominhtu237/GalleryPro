package com.ss.gallerypro.data;

import android.content.Context;

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
}
