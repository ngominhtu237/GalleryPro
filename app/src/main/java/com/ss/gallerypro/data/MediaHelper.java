package com.ss.gallerypro.data;

import android.content.Context;

import java.io.File;

public class MediaHelper {
    private static final String TAG = "MediaHelper";

    public static boolean deleteMedia(Context context, String path) {
        return StorageHelper.deleteMedia(context, new File(path));
    }
}
