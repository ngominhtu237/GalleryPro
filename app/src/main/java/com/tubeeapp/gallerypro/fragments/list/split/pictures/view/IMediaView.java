package com.tubeeapp.gallerypro.fragments.list.split.pictures.view;

import com.tubeeapp.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IMediaView {
    void onGetMediaSuccess(ArrayList<MediaItem> medias);

    void onDeleteMediaSuccess();
}
