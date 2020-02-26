package com.tunm.gallerypro.fragments.list.split.pictures.model;

import android.content.Context;

import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.provider.MediaDataService;
import com.tunm.gallerypro.fragments.list.split.OnMediaDataNotify;

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
