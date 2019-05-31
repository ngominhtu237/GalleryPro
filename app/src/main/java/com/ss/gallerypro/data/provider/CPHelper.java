package com.ss.gallerypro.data.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.AlbumHelper;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.Function;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.filter.AlbumFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CPHelper {

    private static ArrayList<Bucket> mAlbumsList = new ArrayList<>();
    private static ArrayList<MediaItem> mAlbumImages = new ArrayList<>();

    public static ArrayList<Bucket> getAlbums(Context context, ArrayList<Integer> filter) {
        String path, albumName, timestamp, dateTaken, bucketId;
        int countPhoto, countFilter = 0;
        Bucket bucket;
        mAlbumsList.clear();
        Query.Builder query = new Query.Builder()
                .uri(MediaStore.Files.getContentUri("external"))
                .projection(Bucket.getProjection());

        ArrayList<Object> args = new ArrayList<>();
        StringBuilder selection = new StringBuilder();
        for(int i=0; i<filter.size(); i++) {
            if(filter.size() == 0) {
                selection = new StringBuilder("media_type=? ");
                args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_NONE);
            } else {
                if (AlbumFilter.fromValue(filter.get(i)) == AlbumFilter.IMAGE) {
                    countFilter++;
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                }
                if (AlbumFilter.fromValue(filter.get(i)) == AlbumFilter.VIDEO) {
                    countFilter++;
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                }
            }
        }
        for(int c=0; c<countFilter; c++) {
            selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE + "=?");
            if(c+1 != countFilter){
                selection.append(" or ");
            }
        } // => media_type = ? or media_type = ?
        String sel = selection + ") and (";  // sel:  media_type = ? or media_type = ? ) and (
        if(countFilter > 0) {
            selection.append(") group by (parent");
        }
        // selection: media_type = ? or media_type = ? ) group by (parent

        query.selection(selection.toString());
        query.args(args.toArray());

        Cursor cursor;
        if(context != null && !selection.toString().equals("")) {
            cursor = query.build().getCursor(context.getContentResolver());
            Log.v("sql", DatabaseUtils.dumpCursorToString(cursor));
            while(cursor.moveToNext()) {
                bucket = new Bucket();
                bucketId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                // Get path to album
                File filePath = new File(path);
                String dir = "";
                if (!filePath.isDirectory()){
                    dir = filePath.getParentFile().getAbsolutePath();
                }
                bucket.setPathToAlbum(dir);
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                dateTaken = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                bucket.setBucketId(bucketId);
                bucket.setPathPhotoCover(path);
                bucket.setName(albumName);
                bucket.setDateModified(timestamp);
                bucket.setDateTaken(dateTaken);

                countPhoto = Function.getCount(context, sel, args.toArray(), bucketId, albumName);
                bucket.setCount(countPhoto);
                mAlbumsList.add(bucket);
            }
        }
        return mAlbumsList;
    }

    public static void deleteFileFromMediaStore(Context context, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        if(context != null) {
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

    public static ArrayList<MediaItem> getMedias(Context context, String bucketId, String album_name) {

        String path, mediaName, timestamp, size,width, height, mediaType, dateTaken;
        MediaItem item;
        mAlbumImages.clear();
        Uri uriExternal = MediaStore.Files.getContentUri("external");
        String arrNameFilter[] = context.getResources().getStringArray(R.array.list_filter);
        String selection = "bucket_display_name = \"" + album_name + "\"" + " and bucket_id = \"" + bucketId + "\"";
        List<String> filter = new ArrayList<>(AlbumHelper.getFilter(arrNameFilter.length));
        if(filter.size() > 0) {
            if(filter.get(0).equals("0")) {
                selection += " and (media_type=1 ";
                if(filter.size() > 1 && filter.get(1).equals("1")) {
                    selection += " or media_type=3) ";
                } else {
                    selection += ") ";
                }
            }
            if(filter.get(0).equals("1")) {
                selection += " and (media_type=3) ";
            }
        }
        Cursor cursor = context.getContentResolver().query(uriExternal, MediaItem.getProjection(), selection, null, null);
        while(cursor.moveToNext()) {
            item = new MediaItem();
            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            mediaName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));
            mediaType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
            dateTaken = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
            size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
            width = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH));
            height = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT));
            item.setPathMediaItem(path);
            item.setName(mediaName);
            item.setDateModified(timestamp);
            item.setDateTaken(dateTaken);
            item.setSize(size);
            item.setWidth(width);
            item.setHeight(height);
            item.setMediaType(mediaType);
            mAlbumImages.add(item);
        }
        return mAlbumImages;
    }

    public static ArrayList<MediaItem> getVideos(Context context) {
        String path, mediaName, timestamp, size,width, height, mediaType, dateTaken, duration;
        ArrayList<MediaItem> mVideoList = new ArrayList<>();
        Uri uriExternal = MediaStore.Files.getContentUri("external");
        String selection = "media_type = " + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        Cursor cursor = context.getContentResolver().query(uriExternal, MediaItem.getProjection(), selection, null, null);
        MediaItem item;
        while(cursor.moveToNext()) {
            item = new MediaItem();
            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            mediaName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));
            mediaType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
            dateTaken = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
            size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
            width = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH));
            height = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT));
            duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
            item.setPathMediaItem(path);
            item.setName(mediaName);
            item.setDateModified(timestamp);
            item.setDateTaken(dateTaken);
            item.setSize(size);
            item.setWidth(width);
            item.setHeight(height);
            item.setMediaType(mediaType);
            item.setDuration(duration);
            mVideoList.add(item);
        }
        return mVideoList;
    }
}