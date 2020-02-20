package com.tubeeapp.gallerypro.fragments.viewer;

import com.tubeeapp.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface DeletedItemCallback {
    void onDelete(ArrayList<Integer> pos, ArrayList<MediaItem> mediaItems);
}
