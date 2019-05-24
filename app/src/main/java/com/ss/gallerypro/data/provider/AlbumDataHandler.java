package com.ss.gallerypro.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.ss.gallerypro.data.AlbumHelper;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.filter.AlbumFilter;
import com.ss.gallerypro.utils.DataTypeUtils;

import java.util.ArrayList;

public class AlbumDataHandler {
    private ArrayList<Bucket> mBuckets;
    private Context mContext;
    private IAlbumDataChangedCallback dataChangedCallback;

    public AlbumDataHandler(Context context) {
        this.mContext = context;
    }

    public void setDataChangedCallback(IAlbumDataChangedCallback dataChangedCallback) {
        this.dataChangedCallback = dataChangedCallback;
    }

    public void getAlbums() {
        new GetAlbumTask().execute();
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
            dataChangedCallback.processGetDataFinish(mBuckets);
            super.onPostExecute(size);
        }
    }

    private ArrayList<Integer> getFilter() {
        return DataTypeUtils.stringSetToArrayInteger(AlbumHelper.getFilter(AlbumFilter.values().length));
    }
}
