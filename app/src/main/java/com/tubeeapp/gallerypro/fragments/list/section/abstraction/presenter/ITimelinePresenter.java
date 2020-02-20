package com.tubeeapp.gallerypro.fragments.list.section.abstraction.presenter;

import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.data.filter.MediaFilter;

import java.util.ArrayList;

public interface ITimelinePresenter {
    void getMedias(MediaFilter mediaFilter);

    void deleteMedias(ArrayList<MediaItem> mediaItems);
}
