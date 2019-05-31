package com.ss.gallerypro.fragments.list.split.video.view;

import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IVideoView {
    void onGetVideoSuccess(ArrayList<MediaItem> mediaItems);

    void onDeleteVideoSuccess();
}
