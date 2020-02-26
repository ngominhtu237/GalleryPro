package com.tunm.gallerypro.fragments.list.section.abstraction.presenter;

import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.filter.MediaFilter;

import java.util.ArrayList;

public interface ITimelinePresenter {
    void getMedias(MediaFilter mediaFilter);

    void deleteMedias(ArrayList<MediaItem> mediaItems);
}
