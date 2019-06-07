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
import com.ss.gallerypro.data.AlbumHelper;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.fragments.list.normal.albums.OnAlbumDataNotify;
import com.ss.gallerypro.utils.DataTypeUtils;

import java.util.ArrayList;
import java.util.Objects;

public class AlbumDataService {

    private ArrayList<Bucket> mBuckets;
    private Context mContext;
    private OnAlbumDataNotify.GetAlbum getAlbumCallback;
    private OnAlbumDataNotify.DeleteAlbum deleteAlbumCallback;

    public AlbumDataService(Context context) {
        this.mContext = context;
    }


    public void getAlbums(OnAlbumDataNotify.GetAlbum callback) {
        getAlbumCallback = callback;
        new GetAlbumTask().execute();
    }

    public void deleteAlbums(ArrayList<Bucket> albums, OnAlbumDataNotify.DeleteAlbum callback) {
        deleteAlbumCallback = callback;
        new DeleteAlbumTask().execute(albums);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAlbumTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            mBuckets = CPHelper.getAlbums(mContext, getFilter());
            return mBuckets.size();
        }

        @Override
        protected void onPostExecute(Integer size) {
            getAlbumCallback.onResponse(mBuckets);
            super.onPostExecute(size);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteAlbumTask extends AsyncTask<ArrayList<Bucket>, Integer, Integer> {

        Dialog dialog;
        ProgressBar progressBar;
        ArrayList<Bucket> mDeletedAlbums;

        @Override
        protected void onPreExecute() {
            setLockScreenOrientation(true);
            mDeletedAlbums = new ArrayList<>();
            dialog = new Dialog(Objects.requireNonNull(mContext));
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            progressBar = dialog.findViewById(R.id.progressBar1);
            progressBar.setProgressTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.md_blue_grey_700)));
            dialog.show();
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Integer doInBackground(ArrayList<Bucket>... buckets) {
            mDeletedAlbums = buckets[0];
            for(int i=0; i<mDeletedAlbums.size(); i++) {
                if(isCancelled()) break;
                else {
                    AlbumHelper.deleteAlbum(mContext, mDeletedAlbums.get(i).getPathToAlbum());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            deleteAlbumCallback.onResponse();
            dialog.dismiss();
            setLockScreenOrientation(false);
        }
    }

    private ArrayList<Integer> getFilter() {
        return DataTypeUtils.stringSetToArrayInteger(AlbumHelper.getFilter(MediaFilter.values().length));
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
