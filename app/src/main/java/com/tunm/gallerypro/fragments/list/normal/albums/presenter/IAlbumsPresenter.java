package com.tunm.gallerypro.fragments.list.normal.albums.presenter;

import com.tunm.gallerypro.data.Bucket;

import java.util.ArrayList;

interface IAlbumsPresenter {
    void getAlbums();

    void deleteAlbums(ArrayList<Bucket> buckets);

    boolean isListAlbumEmpty(ArrayList<Bucket> buckets);
}
