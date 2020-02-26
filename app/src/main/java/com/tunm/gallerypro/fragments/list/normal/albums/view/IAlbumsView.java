package com.tunm.gallerypro.fragments.list.normal.albums.view;

import com.tunm.gallerypro.data.Bucket;

import java.util.ArrayList;

public interface IAlbumsView {
    void onGetAlbumSuccess(ArrayList<Bucket> buckets);

    void onDeleteAlbumSuccess();
}
