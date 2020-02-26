package com.tunm.gallerypro.data;

import android.content.Context;
import android.media.MediaScannerConnection;

import com.tunm.gallerypro.data.provider.CPHelper;

import java.io.File;
import java.net.URLConnection;

public class StorageHelper {

    private static final String TAG = "StorageHelper";

    public static boolean deleteAlbum(Context context, File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean result = deleteAlbum(context, new File(dir, aChildren));
                if (!result) {
                    return false;
                }
            }
        }
        // Chỉ delete tệp media trong folder và sau khi delete tệp media kiểm tra nếu folder empty thì xóa
        if ((isImageFile(dir.getAbsolutePath()) || isVideoFile(dir.getAbsolutePath())) || (dir.isDirectory() && dir.list().length == 0)) {
            boolean isDeleted = dir.delete();
            if (isDeleted) CPHelper.deleteFileFromMediaStore(context, dir);
            return isDeleted;
        }
        return true;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean deleteMedia(Context context, final File file) {
        boolean success = file.delete(); // Only delete in MyFile but in Gallery it's exists!!!
        if (success) scanFile(context, new String[]{file.getPath()});
        return success;
    }

    private static void scanFile(Context context, String[] paths) {
        MediaScannerConnection.scanFile(context, paths, null, null);
    }
}
