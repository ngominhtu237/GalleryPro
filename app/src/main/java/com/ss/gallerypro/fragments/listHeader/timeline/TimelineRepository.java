package com.ss.gallerypro.fragments.listHeader.timeline;

import android.content.Context;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.provider.MediaDataService;
import com.ss.gallerypro.fragments.listHeader.abstraction.OnTimelineDataNotify;
import com.ss.gallerypro.fragments.listHeader.abstraction.model.ITimelineRepository;

import java.util.ArrayList;

public class TimelineRepository implements ITimelineRepository {
    private MediaDataService mMediaDataService;

    TimelineRepository(Context context) {
        mMediaDataService = new MediaDataService(context);
    }

    @Override
    public void getDataTimeline(OnTimelineDataNotify.Get callback) {
        mMediaDataService.getDataTimeline(callback);
    }

    @Override
    public void deleteDataTimeline(ArrayList<MediaItem> mediaItems, OnTimelineDataNotify.Delete callback) {
        mMediaDataService.deleteDataTimeline(mediaItems, callback);
    }
}
