package com.ss.gallerypro.data.provider;

import com.ss.gallerypro.data.Bucket;

import java.util.ArrayList;

public interface IAlbumDataChangedCallback {
    void processGetDataFinish(ArrayList<Bucket> newBucket);
}
