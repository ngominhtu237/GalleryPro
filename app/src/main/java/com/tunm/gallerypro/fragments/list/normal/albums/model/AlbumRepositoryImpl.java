package com.tunm.gallerypro.fragments.list.normal.albums.model;

import android.content.Context;

import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.data.provider.AlbumDataService;
import com.tunm.gallerypro.fragments.list.normal.albums.OnAlbumDataNotify;

import java.util.ArrayList;

public class AlbumRepositoryImpl implements IAlbumRepository {

    private AlbumDataService mAlbumDataService;
    private Bucket receivedBucket;

    public AlbumRepositoryImpl(Context context) {
        mAlbumDataService = new AlbumDataService(context);
    }

    @Override
    public void getAlbums(OnAlbumDataNotify.GetAlbum callback) {
        mAlbumDataService.getAlbums(callback);
    }

    @Override
    public void deleteAlbums(ArrayList<Bucket> buckets, OnAlbumDataNotify.DeleteAlbum callback) {
        mAlbumDataService.deleteAlbums(buckets, callback);
    }
}
