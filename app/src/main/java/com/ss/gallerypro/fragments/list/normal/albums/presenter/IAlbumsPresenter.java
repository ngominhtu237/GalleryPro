package com.ss.gallerypro.fragments.list.normal.albums.presenter;

import com.ss.gallerypro.data.Bucket;

import java.util.ArrayList;

interface IAlbumsPresenter {
    void getAlbums();

    void deleteAlbums(ArrayList<Bucket> buckets);

    boolean isListAlbumEmpty(ArrayList<Bucket> buckets);
}
