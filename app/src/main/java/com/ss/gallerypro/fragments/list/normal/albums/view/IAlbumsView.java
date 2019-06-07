package com.ss.gallerypro.fragments.list.normal.albums.view;

import com.ss.gallerypro.data.Bucket;

import java.util.ArrayList;

public interface IAlbumsView {
    void onGetAlbumSuccess(ArrayList<Bucket> buckets);

    void onDeleteAlbumSuccess();
}
