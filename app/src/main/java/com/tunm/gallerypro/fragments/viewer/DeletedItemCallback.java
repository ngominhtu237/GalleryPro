package com.tunm.gallerypro.fragments.viewer;

import com.tunm.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface DeletedItemCallback {
    void onDelete(ArrayList<Integer> pos, ArrayList<MediaItem> mediaItems);
}
