package com.tubeeapp.gallerypro.fragments.list.normal.albums.presenter;

import com.tubeeapp.gallerypro.data.Bucket;

import java.util.ArrayList;

interface IAlbumsPresenter {
    void getAlbums();

    void deleteAlbums(ArrayList<Bucket> buckets);

    boolean isListAlbumEmpty(ArrayList<Bucket> buckets);
}
