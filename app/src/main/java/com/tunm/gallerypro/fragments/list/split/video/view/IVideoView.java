package com.tunm.gallerypro.fragments.list.split.video.view;

import com.tunm.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IVideoView {
    void onGetVideoSuccess(ArrayList<MediaItem> mediaItems);

    void onDeleteVideoSuccess();
}
