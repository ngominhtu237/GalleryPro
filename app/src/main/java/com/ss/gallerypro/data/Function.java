package com.ss.gallerypro.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ss.gallerypro.data.utils.DataUtils;

public class Function {

    public static int getCount(Context c, String selection, Object[] args, String bucketId, String album_name) {

        Uri uriExternal = MediaStore.Files.getContentUri("external");
        String[] parseArgs = DataUtils.getStringArgs(args);
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
        String bucket_name = album_name != null ? "= \"" + album_name + "\"" : " is null";
        Cursor cursorExternal = c.getContentResolver().query(
                uriExternal,
                projection,
                selection + "bucket_display_name " + bucket_name + " and bucket_id = \"" + bucketId + "\"",
                parseArgs,
                null);
//        Cursor cursorInternal = c.getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
//        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});


        return cursorExternal.getCount();
    }
}
