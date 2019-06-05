package com.ss.gallerypro.fragments.list.split.video.model;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.fragments.list.split.OnMediaDataNotify;

import java.util.ArrayList;

public interface IVideoRepository {
    void getVideoList(OnMediaDataNotify.GetMedia callback);

    void deleteVideos(ArrayList<MediaItem> mediaItems, OnMediaDataNotify.DeleteMedia callback);
}