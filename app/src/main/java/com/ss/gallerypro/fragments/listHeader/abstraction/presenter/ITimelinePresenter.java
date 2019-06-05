package com.ss.gallerypro.fragments.listHeader.abstraction.presenter;

import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface ITimelinePresenter {
    void getMedias();

    void deleteMedias(ArrayList<MediaItem> mediaItems);
}
