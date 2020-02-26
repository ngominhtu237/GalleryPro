package com.tunm.gallerypro.fragments.list.normal.albums.model;

import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.fragments.list.normal.albums.OnAlbumDataNotify;

import java.util.ArrayList;

public interface IAlbumRepository {
    void getAlbums(OnAlbumDataNotify.GetAlbum callback);

    void deleteAlbums(ArrayList<Bucket> albums, OnAlbumDataNotify.DeleteAlbum callback);
}
