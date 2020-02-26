package com.tunm.gallerypro.fragments.list.section.abstraction.model;

import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.filter.MediaFilter;
import com.tunm.gallerypro.fragments.list.section.abstraction.OnTimelineDataNotify;

import java.util.ArrayList;

public interface ITimelineRepository {
    void getDataTimeline(MediaFilter mediaFilter, OnTimelineDataNotify.Get callback);

    void deleteDataTimeline(ArrayList<MediaItem> mediaItems, OnTimelineDataNotify.Delete callback);
}
