package com.ss.gallerypro.fragments.listHeader.abstraction;

import com.ss.gallerypro.data.MediaItem;

public class ContentModel implements ItemInterface {

    public MediaItem mMediaItem;

    public ContentModel(MediaItem mediaItem) {
        this.mMediaItem = mediaItem;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
