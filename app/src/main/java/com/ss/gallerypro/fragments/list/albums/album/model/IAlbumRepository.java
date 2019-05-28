package com.ss.gallerypro.fragments.list.albums.album.model;

import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.fragments.list.albums.album.OnAlbumDataNotify;

import java.util.ArrayList;

public interface IAlbumRepository {
    void getAlbums(OnAlbumDataNotify.GetAlbum callback);

    void deleteAlbums(ArrayList<Bucket> albums, OnAlbumDataNotify.DeleteAlbum callback);
}
