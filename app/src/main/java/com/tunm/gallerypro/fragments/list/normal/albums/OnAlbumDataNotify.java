package com.tunm.gallerypro.fragments.list.normal.albums;

import com.tunm.gallerypro.data.Bucket;

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
