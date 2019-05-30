package com.ss.gallerypro.data.provider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Window;
import android.widget.ProgressBar;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.MediaHelper;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.sort.PhotoComparators;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.list.pictures.OnMediaDataNotify;

import java.util.ArrayList;

public class MediaDataService {
    private Context mContext;
    private ArrayList<MediaItem> mMedias;
    private OnMediaDataNotify.GetMedia getMediaCallback;
    private OnMediaDataNotify.DeleteMedia deleteMediaCallback;

    public MediaDataService(Context context) {
        this.mContext = context;
    }

    public void getMedia(Bucket bucket, OnMediaDataNotify.GetMedia callback) {
        getMediaCallback = callback;
        new GetMediaTask().execute(bucket);
    }

    public void deleteMedias(ArrayList<MediaItem> medias, OnMediaDataNotify.DeleteMedia callback) {
        deleteMediaCallback = callback;
        new DeleteMediaTask().execute(medias);
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
            mMedias.sort(PhotoComparators.getComparator(SortingMode.DATE, SortingOrder.DESCENDING));
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

        Dialog dialog;
        ProgressBar progressBar;
        ArrayList<MediaItem> mDeletedItems;

        @Override
        protected void onPreExecute() {
            setLockScreenOrientation(true);
            mDeletedItems = new ArrayList<>();
            dialog = new Dialog(mContext);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            progressBar = dialog.findViewById(R.id.progressBar1);
            progressBar.setProgressTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.md_blue_grey_700)));
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
