package com.ss.gallerypro.data.provider;

import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IMediaDataChangeCallback {
    void processGetDataFinish(ArrayList<MediaItem> newMedias);

    void processDeleteFinish();
}
