package com.tubeeapp.gallerypro.fragments.list.normal.albums.view;

import com.tubeeapp.gallerypro.data.Bucket;

import java.util.ArrayList;

public interface IAlbumsView {
    void onGetAlbumSuccess(ArrayList<Bucket> buckets);

    void onDeleteAlbumSuccess();
}
