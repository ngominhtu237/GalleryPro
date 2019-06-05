package com.ss.gallerypro.fragments.listHeader.abstraction;

import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface OnTimelineDataNotify {
    interface Get {
        void onResponse(ArrayList<MediaItem> buckets);

        void onError(String errMsg);
    }

    interface Delete {
        void onResponse();

        void onError(String errMsg);
    }
}
