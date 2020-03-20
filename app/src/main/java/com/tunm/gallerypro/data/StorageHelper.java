package com.tunm.gallerypro.data;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
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
            if (isDeleted) deleteFileFromMediaStore(context, dir);
            return isDeleted;
        }
        return true;
    }

    public static boolean isImageFile(String fname) {
        String mimeType = URLConnection.guessContentTypeFromName(fname);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String fname) {
        String mimeType = URLConnection.guessContentTypeFromName(fname);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean deleteMedia(Context context, final File file) {
        boolean success;
        File f = new File(file.getPath());
        if (success = ContentHelper.deleteFile(context, f)) {
            deleteFileFromMediaStore(context, f);
        }
        return success;
    }

    public static void deleteFileFromMediaStore(Context context, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        if (context != null) {
            final int result = context.getContentResolver().delete(uri,
                    MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
            if (result == 0) {
                final String absolutePath = file.getAbsolutePath();
                if (!absolutePath.equals(canonicalPath)) {
                    context.getContentResolver().delete(uri,
                            MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
                }
            }
        }
    }
}
