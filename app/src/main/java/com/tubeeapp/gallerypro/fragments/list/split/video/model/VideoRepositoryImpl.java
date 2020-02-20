package com.tubeeapp.gallerypro.fragments.list.split.video.model;

import android.content.Context;

import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.data.provider.MediaDataService;
import com.tubeeapp.gallerypro.fragments.list.split.OnMediaDataNotify;

import java.util.ArrayList;

public class VideoRepositoryImpl implements IVideoRepository {

    private MediaDataService mMediaDataService;

    public VideoRepositoryImpl(Context context) {
        mMediaDataService = new MediaDataService(context);
    }

    @Override
    public void getVideoList(OnMediaDataNotify.GetMedia callback) {
        mMediaDataService.getVideoList(callback);
    }

    @Override
    public void deleteVideos(ArrayList<MediaItem> mediaItems, OnMediaDataNotify.DeleteMedia callback) {
        mMediaDataService.deleteMedias(mediaItems, callback);
    }
}
