package com.ss.gallerypro.fragments.viewer;

import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface DeletedItemCallback {
    void onDelete(ArrayList<Integer> pos, ArrayList<MediaItem> mediaItems);
}
