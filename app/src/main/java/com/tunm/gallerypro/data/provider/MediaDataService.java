package com.tunm.gallerypro.data.provider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Window;

import com.tunm.gallerypro.R;
import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.data.MediaHelper;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.filter.MediaFilter;
import com.tunm.gallerypro.data.sort.PhotoComparators;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.fragments.list.split.OnMediaDataNotify;
import com.tunm.gallerypro.fragments.list.section.abstraction.OnTimelineDataNotify;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.IItem;
import com.tunm.gallerypro.theme.ui.ProgressBarTheme;
import com.tunm.gallerypro.theme.ui.SpinKitViewTheme;
import com.tunm.gallerypro.view.dialog.ProgressDialogTheme;

import java.util.ArrayList;

public class MediaDataService {
    private Context mContext;
    private ArrayList<MediaItem> mMedias;
    private OnMediaDataNotify.GetMedia getMediaCallback;
    private OnMediaDataNotify.DeleteMedia deleteMediaCallback;

    // Timeline
    private ArrayList<IItem> list;
    private OnTimelineDataNotify.Get getTimelineDataCb;
    private OnTimelineDataNotify.Delete deleteTimelineDataCb;
    private MediaFilter mediaFilter;

    public MediaDataService(Context context) {
        this.mContext = context;
    }

    public void getMedia(Bucket bucket, OnMediaDataNotify.GetMedia callback) {
        getMediaCallback = callback;
        new GetMediaTask().execute(bucket);
    }

    public void getVideoList(OnMediaDataNotify.GetMedia callback) {
        getMediaCallback = callback;
        new GetVideoTask().execute();
    }

    public void deleteMedias(ArrayList<MediaItem> medias, OnMediaDataNotify.DeleteMedia callback) {
        deleteMediaCallback = callback;
        new DeleteMediaTask().execute(medias);
    }

    public void getDataTimeline(MediaFilter mediaFilter, OnTimelineDataNotify.Get callback) {
        this.mediaFilter = mediaFilter;
        getTimelineDataCb = callback;
        new GetTimelineDataTask().execute();
    }

    public void deleteDataTimeline(ArrayList<MediaItem> medias, OnTimelineDataNotify.Delete callback) {
        deleteTimelineDataCb = callback;
        new DeleteTimelineDataTask().execute(medias);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetTimelineDataTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mMedias = CPHelper.getMediaTimeline(mContext, mediaFilter);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getTimelineDataCb.onResponse(mMedias);
            super.onPostExecute(aVoid);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteTimelineDataTask extends AsyncTask<ArrayList<MediaItem>, Integer, Integer> {

        ProgressDialogTheme dialog;
        ProgressBarTheme spinKit;
        ArrayList<MediaItem> mDeletedItems;

        @Override
        protected void onPreExecute() {
            setLockScreenOrientation(true);
            mDeletedItems = new ArrayList<>();
            dialog = new ProgressDialogTheme(mContext);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            spinKit = dialog.findViewById(R.id.spin_delete_items);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(ArrayList<MediaItem>... items) {
            mDeletedItems = items[0];
            for(int i=0; i<mDeletedItems.size(); i++) {
                if(isCancelled()) break;
                else {
                    MediaHelper.deleteMedia(mContext, mDeletedItems.get(i).getPathMediaItem());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            deleteTimelineDataCb.onResponse();
            dialog.dismiss();
            setLockScreenOrientation(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetMediaTask extends AsyncTask<Bucket, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Bucket... buckets) {
            Bucket mReceiveBucket = buckets[0];
            mMedias = CPHelper.getMedias(mContext, mReceiveBucket.getBucketId(), mReceiveBucket.getName());
            mMedias.sort(PhotoComparators.getComparator(SortingMode.DATE_TAKEN, SortingOrder.DESCENDING));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getMediaCallback.onResponse(mMedias);
            super.onPostExecute(aVoid);
        }
    }

    private class GetVideoTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mMedias = CPHelper.getVideos(mContext);
            mMedias.sort(PhotoComparators.getComparator(SortingMode.DATE_TAKEN, SortingOrder.DESCENDING));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getMediaCallback.onResponse(mMedias);
            super.onPostExecute(aVoid);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteMediaTask extends AsyncTask<ArrayList<MediaItem>, Integer, Integer> {

        ProgressDialogTheme dialog;
        SpinKitViewTheme spinKit;
        ArrayList<MediaItem> mDeletedItems;

        @Override
        protected void onPreExecute() {
            setLockScreenOrientation(true);
            mDeletedItems = new ArrayList<>();
            dialog = new ProgressDialogTheme(mContext);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            spinKit = dialog.findViewById(R.id.spin_delete_items);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(ArrayList<MediaItem>... items) {
            mDeletedItems = items[0];
            for(int i=0; i<mDeletedItems.size(); i++) {
                if(isCancelled()) break;
                else {
                    MediaHelper.deleteMedia(mContext, mDeletedItems.get(i).getPathMediaItem());
                    mMedias.remove(mDeletedItems.get(i)); // no need
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            deleteMediaCallback.onResponse();
            dialog.dismiss();
            setLockScreenOrientation(false);
        }
    }

    private void setLockScreenOrientation(boolean lock) {
        if (Build.VERSION.SDK_INT >= 18) {
            ((Activity)mContext).setRequestedOrientation(lock? ActivityInfo.SCREEN_ORIENTATION_LOCKED:ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            return;
        }

        if (lock) {
            switch (((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation()) {
                case 0: ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break; // value 1
                case 2: ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); break; // value 9
                case 1: ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break; // value 0
                case 3: ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); break; // value 8
            }
        } else
            ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR); // value 10
    }
}
