package com.tunm.gallerypro.fragments.list.split.pictures.presenter;

import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.data.MediaItem;

import java.util.ArrayList;

public interface IMediaPresenter {
    void getMedias(Bucket bucket);

    void deleteMedias(ArrayList<MediaItem> medias);
}
