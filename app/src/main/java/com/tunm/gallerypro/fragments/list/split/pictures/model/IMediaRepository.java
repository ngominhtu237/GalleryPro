package com.tunm.gallerypro.fragments.list.split.pictures.model;

import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.fragments.list.split.OnMediaDataNotify;

import java.util.ArrayList;

public interface IMediaRepository {
    void getMedias(Bucket bucket, OnMediaDataNotify.GetMedia callback);

    void deleteMedias(ArrayList<MediaItem> medias, OnMediaDataNotify.DeleteMedia callback);
}
