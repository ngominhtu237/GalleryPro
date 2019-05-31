package com.ss.gallerypro.fragments.list.split.pictures.view;

import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IMediaView {
    void onGetMediaSuccess(ArrayList<MediaItem> medias);

    void onDeleteMediaSuccess();
}
