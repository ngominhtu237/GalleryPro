package com.tubeeapp.gallerypro.fragments.list.split.pictures.presenter;

import com.tubeeapp.gallerypro.data.Bucket;
import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.fragments.list.split.OnMediaDataNotify;
import com.tubeeapp.gallerypro.fragments.list.split.pictures.model.IMediaRepository;
import com.tubeeapp.gallerypro.fragments.list.split.pictures.view.IMediaView;

import java.util.ArrayList;

public class MediaPresenterImpl implements IMediaPresenter{

    private IMediaView mView;
    private IMediaRepository mRepository;

    public MediaPresenterImpl(IMediaView mView, IMediaRepository mRepository) {
        this.mView = mView;
        this.mRepository = mRepository;
    }

    @Override
    public void getMedias(Bucket bucket) {
        mRepository.getMedias(bucket, getMediaCallback);
    }

    @Override
    public void deleteMedias(ArrayList<MediaItem> medias) {
        mRepository.deleteMedias(medias, deleteMediaCallback);
    }

    private final OnMediaDataNotify.GetMedia getMediaCallback = new OnMediaDataNotify.GetMedia() {
        @Override
        public void onResponse(ArrayList<MediaItem> medias) {
            mView.onGetMediaSuccess(medias);
        }

        @Override
        public void onError(String errMsg) {

        }
    };

    private final OnMediaDataNotify.DeleteMedia deleteMediaCallback = new OnMediaDataNotify.DeleteMedia() {
        @Override
        public void onResponse() {
            mView.onDeleteMediaSuccess();
        }

        @Override
        public void onError(String errMsg) {

        }
    };
}
