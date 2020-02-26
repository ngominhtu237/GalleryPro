package com.tunm.gallerypro.fragments.list.section.abstraction.view;

import com.tunm.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface ITimelineView {
    void onGetTimelineSuccess(ArrayList<MediaItem> mediaItems);

    void onDeleteTimelineSuccess();
}
