package com.ss.gallerypro.fragments.list.split.video.presenter;

import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IVideoPresenter {
    void getVideoList();

    void deleteMedias(ArrayList<MediaItem> medias);
}
