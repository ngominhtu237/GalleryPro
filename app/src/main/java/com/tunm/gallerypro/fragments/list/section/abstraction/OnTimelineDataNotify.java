package com.tunm.gallerypro.fragments.list.section.abstraction;

import com.tunm.gallerypro.data.MediaItem;

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
