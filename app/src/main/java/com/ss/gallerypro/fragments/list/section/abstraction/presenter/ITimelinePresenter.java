package com.ss.gallerypro.fragments.list.section.abstraction.presenter;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.filter.MediaFilter;

import java.util.ArrayList;

public interface ITimelinePresenter {
    void getMedias(MediaFilter mediaFilter);

    void deleteMedias(ArrayList<MediaItem> mediaItems);
}
