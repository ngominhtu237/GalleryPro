package com.ss.gallerypro.fragments.list.pictures.presenter;

import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IMediaPresenter {
    void getMedias(Bucket bucket);

    void deleteMedias(ArrayList<MediaItem> medias);
}
