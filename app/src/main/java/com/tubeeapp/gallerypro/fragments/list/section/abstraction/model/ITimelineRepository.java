package com.tubeeapp.gallerypro.fragments.list.section.abstraction.model;

import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.data.filter.MediaFilter;
import com.tubeeapp.gallerypro.fragments.list.section.abstraction.OnTimelineDataNotify;

import java.util.ArrayList;

public interface ITimelineRepository {
    void getDataTimeline(MediaFilter mediaFilter, OnTimelineDataNotify.Get callback);

    void deleteDataTimeline(ArrayList<MediaItem> mediaItems, OnTimelineDataNotify.Delete callback);
}
