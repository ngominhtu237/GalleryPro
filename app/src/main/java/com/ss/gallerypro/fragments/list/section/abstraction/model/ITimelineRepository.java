package com.ss.gallerypro.fragments.list.section.abstraction.model;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.fragments.list.section.abstraction.OnTimelineDataNotify;

import java.util.ArrayList;

public interface ITimelineRepository {
    void getDataTimeline(MediaFilter mediaFilter, OnTimelineDataNotify.Get callback);

    void deleteDataTimeline(ArrayList<MediaItem> albums, OnTimelineDataNotify.Delete callback);
}
