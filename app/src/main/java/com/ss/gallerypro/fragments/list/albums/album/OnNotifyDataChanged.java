package com.ss.gallerypro.fragments.list.albums.album;

import com.ss.gallerypro.data.Bucket;

import java.util.ArrayList;

// notify data to view when data in adapter is changed
// Eg: when sort in adapter => data in adapter is changed => need to update to RecycleView
interface OnNotifyDataChanged {
    void updateDataToView(ArrayList<Bucket> newBucket);
}
