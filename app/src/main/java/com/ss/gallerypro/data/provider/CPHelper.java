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
import com.ss.gallerypro.data.StatisticModel;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.fragments.list.section.abstraction.model.ContentModel;
import com.ss.gallerypro.fragments.list.section.abstraction.model.HeaderModel;
import com.ss.gallerypro.fragments.list.section.abstraction.model.IItem;
import com.ss.gallerypro.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class CPHelper {

    private static ArrayList<Bucket> mAlbumsList = new ArrayList<>();
    private static ArrayList<MediaItem> mAlbumImages = new ArrayList<>();
    public static final String EXTERNAL_STORAGE_ROOT_NAME = "root";

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
        for (int i = 0; i < filter.size(); i++) {
            if (filter.size() == 0) {
                selection = new StringBuilder("media_type=? ");
                args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_NONE);
            } else {
                if (MediaFilter.fromValue(filter.get(i)) == MediaFilter.IMAGE) {
                    countFilter++;
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                }
                if (MediaFilter.fromValue(filter.get(i)) == MediaFilter.VIDEO) {
                    countFilter++;
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                }
            }
        }
        for (int c = 0; c < countFilter; c++) {
            selection.append(MediaStore.Files.FileColumns.MEDIA_TYPE + "=?");
            if (c + 1 != countFilter) {
                selection.append(" or ");
            }
        } // => media_type = ? or media_type = ?
        String sel = selection + ") and (";  // sel:  media_type = ? or media_type = ? ) and (
        if (countFilter > 0) {
            selection.append(") group by (parent");
        }
        // selection: media_type = ? or media_type = ? ) group by (parent

        query.selection(selection.toString());
        query.args(args.toArray());

        Cursor cursor;
        if (context != null && !selection.toString().equals("")) {
            cursor = query.build().getCursor(context.getContentResolver());
            Log.v("sql", DatabaseUtils.dumpCursorToString(cursor));
            while (cursor.moveToNext()) {
                bucket = new Bucket();
                bucketId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                // Get path to album
                File filePath = new File(path);
                String dir = "";
                if (!filePath.isDirectory()) {
                    dir = filePath.getParentFile().getAbsolutePath();
                }
                bucket.setPathToAlbum(dir);
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                dateTaken = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                bucket.setBucketId(bucketId);
                bucket.setPathPhotoCover(path);
                bucket.setName(albumName != null ? albumName : EXTERNAL_STORAGE_ROOT_NAME);
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

    public static ArrayList<MediaItem> getMedias(Context context, String bucketId, String album_name) {

        String path, mediaName, timestamp, size, width, height, mediaType, dateTaken;
        MediaItem item;
        mAlbumImages.clear();
        Uri uriExternal = MediaStore.Files.getContentUri("external");
        String arrNameFilter[] = context.getResources().getStringArray(R.array.list_filter);
        String bucket_name = !album_name.equals(CPHelper.EXTERNAL_STORAGE_ROOT_NAME) ? "= \"" + album_name + "\"" : " is null";
        String selection = "bucket_display_name " + bucket_name + " and bucket_id = \"" + bucketId + "\"";
        List<String> filter = new ArrayList<>(AlbumHelper.getFilter(arrNameFilter.length));
        if (filter.size() > 0) {
            if (filter.get(0).equals("0")) {
                selection += " and (media_type=1 ";
                if (filter.size() > 1 && filter.get(1).equals("1")) {
                    selection += " or media_type=3) ";
                } else {
                    selection += ") ";
                }
            }
            if (filter.get(0).equals("1")) {
                selection += " and (media_type=3) ";
            }
        }
        Cursor cursor = context.getContentResolver().query(uriExternal, MediaItem.getProjection(), selection, null, null);
        while (cursor.moveToNext()) {
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
        String path, mediaName, timestamp, size, width, height, mediaType, dateTaken, duration;
        ArrayList<MediaItem> mVideoList = new ArrayList<>();
        Uri uriExternal = MediaStore.Files.getContentUri("external");
        String selection = "media_type = " + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        Cursor cursor = context.getContentResolver().query(uriExternal, MediaItem.getProjection(), selection, null, null);
        MediaItem item;
        while (cursor.moveToNext()) {
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

    public static ArrayList<MediaItem> getMediaTimeline(Context context, MediaFilter mediaFilter) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        Uri uriExternal = MediaStore.Files.getContentUri("external");
        String selection;
        if(mediaFilter == MediaFilter.IMAGE) {
            selection = "media_type = 1";
        } else {
            selection = "media_type = 3";
        }
        Cursor cursor = context.getContentResolver().query(uriExternal, MediaItem.getProjection(), selection, null, null);
        MediaItem item;
        while (cursor != null && cursor.moveToNext()) {
            item = new MediaItem();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            String mediaName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));
            String mediaType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
            String dateTaken = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
            String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
            String width = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH));
            String height = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
            item.setPathMediaItem(path);
            item.setName(mediaName);
            item.setDateModified(timestamp);
            item.setDateTaken(dateTaken);
            item.setSize(size);
            item.setWidth(width);
            item.setHeight(height);
            item.setMediaType(mediaType);
            item.setDuration(duration);
            mediaItems.add(item);
        }
        return mediaItems;
    }

    private static ArrayList<IItem> getListData(ArrayList<MediaItem> mediaItems, SortingMode sortingMode) {

        switch (sortingMode) {
            case DATE_TAKEN:
                return createDataSortByDateTaken(mediaItems);
            case LAST_MODIFIED:
                return createDataSortByLastModified(mediaItems);
            case NAME:
                return createDataSortByName(mediaItems);
            case SIZE:
                return createDataSortBySize(mediaItems);
        }
        return null;
    }

    public static TreeMap<Integer, String> sizeSortAsc = new TreeMap<Integer, String>() {{
        put(0, "Very Tiny Size (<1MB)");
        put(1, "Tiny Size (>=1MB & <5MB)");
        put(2, "Very Small Size (>=5MB & <50MB)");
        put(3, "Small Size (>=50MB & <100MB)");
        put(4, "Medium Size (>=100MB)");
    }};


    public static ArrayList<IItem> createDataSortBySize(ArrayList<MediaItem> mediaItems) {
        ArrayList<IItem> list = new ArrayList<>();
        boolean c1 = false, c2 = false, c3 = false, c4 = false, c5 = false;
        for (int i = 0; i < mediaItems.size(); i++) {
            Long temp = Long.valueOf(mediaItems.get(i).getSize()) / (1024L * 1024L);
            if (temp < 1) {
                if (!c1) {
                    HeaderModel section = new HeaderModel(sizeSortAsc.get(0));
                    list.add(section);
                    c1 = true;
                }
                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            }
            if (temp >= 1 && temp < 5) {
                if (!c2) {
                    HeaderModel section = new HeaderModel(sizeSortAsc.get(1));
                    list.add(section);
                    c2 = true;
                }
                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            }
            if (temp >= 5 && temp < 50) {
                if (!c3) {
                    HeaderModel section = new HeaderModel(sizeSortAsc.get(2));
                    list.add(section);
                    c3 = true;
                }
                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            }
            if (temp >= 50 && temp < 100) {
                if (!c4) {
                    HeaderModel section = new HeaderModel(sizeSortAsc.get(3));
                    list.add(section);
                    c4 = true;
                }
                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            }
            if (temp >= 100) {
                if (!c5) {
                    HeaderModel section = new HeaderModel(sizeSortAsc.get(4));
                    list.add(section);
                    c5 = true;
                }
                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            }
        }
        return list;
    }

    public static ArrayList<IItem> createDataSortByName(ArrayList<MediaItem> mediaItems) {
        ArrayList<IItem> list = new ArrayList<>();
        char currentName = '@';
        for (int i = 0; i < mediaItems.size(); i++) {
            char c = mediaItems.get(i).getName().charAt(0);
            if (currentName != c) {
                HeaderModel section = new HeaderModel(String.valueOf(c));
                currentName = c;
                list.add(section);

                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            } else {
                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            }
        }
        return list;
    }

    public static ArrayList<IItem> createDataSortByLastModified(ArrayList<MediaItem> mediaItems) {
        ArrayList<IItem> list = new ArrayList<>();
        String currentLastModified = "-1";
        for (int i = 0; i < mediaItems.size(); i++) {
            String temp = Convert.Epoch2DateString(Long.parseLong(mediaItems.get(i).getDateModified()));
            if (!currentLastModified.equals(temp)) {
                HeaderModel section = new HeaderModel(temp);
                currentLastModified = temp;
                list.add(section);

                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            } else {
                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            }
        }
        return list;
    }

    public static ArrayList<IItem> createDataSortByDateTaken(ArrayList<MediaItem> mediaItems) {
        ArrayList<IItem> list = new ArrayList<>();
        String currentDateTaken = "-1";
        String tempDateTaken;
        for (int i = 0; i < mediaItems.size(); i++) {
//            tempDateTaken = Convert.Epoch2DateString(Long.parseLong(mediaItems.get(i).getDateTaken()));
            if (mediaItems.get(i).getDateTaken() != null && !currentDateTaken.equals(Convert.Epoch2DateString(Long.parseLong(mediaItems.get(i).getDateTaken())))) {
                tempDateTaken = Convert.Epoch2DateString(Long.parseLong(mediaItems.get(i).getDateTaken()));
                HeaderModel section = new HeaderModel(Convert.Epoch2DateString(Long.parseLong(mediaItems.get(i).getDateTaken())));
                currentDateTaken = tempDateTaken;
                list.add(section);

                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            } else {
                ContentModel content = new ContentModel(mediaItems.get(i));
                list.add(content);
            }
        }
        return list;
    }

    public static StatisticModel getStatisticsImage(Context context, MediaFilter mediaFilter) {
        Uri uriExternal = MediaStore.Files.getContentUri("external");

        String projection[] = new String[]{
                "count(_id)",
                "sum(" + MediaStore.MediaColumns.SIZE + ")"
        };

        String selection;
        if(mediaFilter == MediaFilter.IMAGE) {
            selection = "media_type = 1";
        } else {
            selection = "media_type = 3";
        }

        Cursor cursor = context.getContentResolver().query(uriExternal, projection, selection, null, null);
        StatisticModel item = null;
        while (cursor != null && cursor.moveToNext()) {
            item = new StatisticModel();
            int count = cursor.getInt(0);
            long sum = cursor.getLong(1);
            item.setCount(count);
            item.setSize(sum);
        }
        return item;
    }

    public static StatisticModel getStatisticsAlbum(Context context) {
        Uri uriExternal = MediaStore.Files.getContentUri("external");

        String projectionSize[] = new String[]{
                "sum(" + MediaStore.MediaColumns.SIZE + ")"
        };
        String s = "media_type = 1 or media_type = 3";

        Cursor c = context.getContentResolver().query(uriExternal, projectionSize, s, null, null);
        StatisticModel item = new StatisticModel();
        if (c != null && c.moveToNext()) {
            long sum = c.getLong(0);
            item.setSize(sum);
        }

        String projectionCount[] = new String[]{
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                //"count(" + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + ")"
        };
        String s1 = "media_type = 1 or media_type = 3 ) group by ( parent";
        Cursor c1 = context.getContentResolver().query(uriExternal, projectionCount, s1, null, null);
        if (c1 != null) {
            item.setCount(c1.getCount());
        }
        return item;
    }
}