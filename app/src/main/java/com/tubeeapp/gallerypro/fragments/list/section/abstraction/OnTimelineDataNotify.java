package com.tubeeapp.gallerypro.fragments.list.section.abstraction;

import com.tubeeapp.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface OnTimelineDataNotify {
    interface Get {
        void onResponse(ArrayList<MediaItem> list);

        void onError(String errMsg);
    }

    interface Delete {
        void onResponse();

        void onError(String errMsg);
    }
}
