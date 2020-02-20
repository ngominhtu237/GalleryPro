package com.tubeeapp.gallerypro.fragments.list.section.abstraction.view;

import com.tubeeapp.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface ITimelineView {
    void onGetTimelineSuccess(ArrayList<MediaItem> mediaItems);

    void onDeleteTimelineSuccess();
}
