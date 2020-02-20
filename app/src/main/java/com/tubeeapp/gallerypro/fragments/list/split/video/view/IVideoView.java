package com.tubeeapp.gallerypro.fragments.list.split.video.view;

import com.tubeeapp.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IVideoView {
    void onGetVideoSuccess(ArrayList<MediaItem> mediaItems);

    void onDeleteVideoSuccess();
}
