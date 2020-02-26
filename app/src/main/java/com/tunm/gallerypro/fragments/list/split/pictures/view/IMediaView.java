package com.tunm.gallerypro.fragments.list.split.pictures.view;

import com.tunm.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IMediaView {
    void onGetMediaSuccess(ArrayList<MediaItem> medias);

    void onDeleteMediaSuccess();
}
