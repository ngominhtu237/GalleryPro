package com.tunm.gallerypro.fragments.list.split;

import com.tunm.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface OnMediaDataNotify {
    interface GetMedia {
        void onResponse(ArrayList<MediaItem> medias);

        void onError(String errMsg);
    }

    interface DeleteMedia {
        void onResponse();

        void onError(String errMsg);
    }
}
