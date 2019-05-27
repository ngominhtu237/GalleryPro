package com.ss.gallerypro.fragments.list.albums.pictures;

import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

interface OnNotifyDataChanged {
    void updateDataToView(ArrayList<MediaItem> mImageList);
}
