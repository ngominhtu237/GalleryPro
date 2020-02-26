package com.tunm.gallerypro.fragments.list.split.video.presenter;

import com.tunm.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IVideoPresenter {
    void getVideoList();

    void deleteMedias(ArrayList<MediaItem> medias);
}
