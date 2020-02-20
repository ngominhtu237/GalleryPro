package com.tubeeapp.gallerypro.fragments.list.section.abstraction.model;

import com.tubeeapp.gallerypro.data.MediaItem;

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
