package com.ss.gallerypro.fragments.listHeader.video;

import android.content.Context;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.data.provider.MediaDataService;
import com.ss.gallerypro.fragments.listHeader.abstraction.OnTimelineDataNotify;
import com.ss.gallerypro.fragments.listHeader.abstraction.model.ITimelineRepository;

import java.util.ArrayList;

public class VideoRepository implements ITimelineRepository {
    private MediaDataService mMediaDataService;

    VideoRepository(Context context) {
        mMediaDataService = new MediaDataService(context);
    }

    @Override
    public void getDataTimeline(MediaFilter mediaFilter, OnTimelineDataNotify.Get callback) {
        mMediaDataService.getDataTimeline(mediaFilter, callback);
    }

    @Override
    public void deleteDataTimeline(ArrayList<MediaItem> mediaItems, OnTimelineDataNotify.Delete callback) {
        mMediaDataService.deleteDataTimeline(mediaItems, callback);
    }
}
