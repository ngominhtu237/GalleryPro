package com.tubeeapp.gallerypro.fragments.list.split.pictures.presenter;

import com.tubeeapp.gallerypro.data.Bucket;
import com.tubeeapp.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IMediaPresenter {
    void getMedias(Bucket bucket);

    void deleteMedias(ArrayList<MediaItem> medias);
}
