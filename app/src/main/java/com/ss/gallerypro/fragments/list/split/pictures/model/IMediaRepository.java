package com.ss.gallerypro.fragments.list.split.pictures.model;

import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.fragments.list.split.OnMediaDataNotify;

import java.util.ArrayList;

public interface IMediaRepository {
    void getMedias(Bucket bucket, OnMediaDataNotify.GetMedia callback);

    void deleteMedias(ArrayList<MediaItem> medias, OnMediaDataNotify.DeleteMedia callback);
}
