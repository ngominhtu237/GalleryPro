package com.ss.gallerypro.fragments.list.pictures.model;

import android.content.Context;

import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.provider.MediaDataService;
import com.ss.gallerypro.fragments.list.pictures.OnMediaDataNotify;

import java.util.ArrayList;

public class MediaRepositoryImpl implements IMediaRepository{

    private MediaDataService mMediaDataService;

    public MediaRepositoryImpl(Context context) {
        mMediaDataService = new MediaDataService(context);
    }

    @Override
    public void getMedias(Bucket bucket, OnMediaDataNotify.GetMedia callback) {
        mMediaDataService.getMedia(bucket, callback);
    }

    @Override
    public void deleteMedias(ArrayList<MediaItem> medias, OnMediaDataNotify.DeleteMedia callback) {
        mMediaDataService.deleteMedias(medias, callback);
    }
}
