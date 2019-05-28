package com.ss.gallerypro.fragments.list.albums.album.presenter;

import com.ss.gallerypro.data.Bucket;

import java.util.ArrayList;

interface IAlbumsPresenter {
    void getAlbums();

    void deleteAlbums(ArrayList<Bucket> buckets);

    boolean isListAlbumEmpty(ArrayList<Bucket> buckets);
}
