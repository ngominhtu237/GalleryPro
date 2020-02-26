package com.tunm.gallerypro.fragments.list.section.video;

import android.content.Context;

import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.filter.MediaFilter;
import com.tunm.gallerypro.data.provider.MediaDataService;
import com.tunm.gallerypro.fragments.list.section.abstraction.OnTimelineDataNotify;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;

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
