package com.tunm.gallerypro.fragments.list.section.abstraction.model;

import com.tunm.gallerypro.data.MediaItem;

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
