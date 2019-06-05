package com.ss.gallerypro.fragments.listHeader.abstraction.model;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.fragments.listHeader.abstraction.OnTimelineDataNotify;

import java.util.ArrayList;

public interface ITimelineRepository {
    void getDataTimeline(OnTimelineDataNotify.Get callback);

    void deleteDataTimeline(ArrayList<MediaItem> albums, OnTimelineDataNotify.Delete callback);
}