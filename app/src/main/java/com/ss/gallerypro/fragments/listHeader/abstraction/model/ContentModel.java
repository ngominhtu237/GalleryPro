package com.ss.gallerypro.fragments.listHeader.abstraction.model;

import com.ss.gallerypro.data.MediaItem;

public class ContentModel implements IItem {

    public MediaItem mMediaItem;

    public ContentModel(MediaItem mediaItem) {
        this.mMediaItem = mediaItem;
    }

    @Override
    public boolean isHeader() {
        return false;
    }
}
