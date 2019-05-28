package com.ss.gallerypro.fragments.list.albums.album;

import com.ss.gallerypro.data.Bucket;

import java.util.ArrayList;

public interface OnAlbumDataNotify {

    interface GetAlbum {
        void onResponse(ArrayList<Bucket> buckets);

        void onError(String errMsg);
    }

    interface DeleteAlbum {
        void onResponse();

        void onError(String errMsg);
    }
}
