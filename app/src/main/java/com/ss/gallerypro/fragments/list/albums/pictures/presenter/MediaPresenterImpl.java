package com.ss.gallerypro.fragments.list.albums.pictures.presenter;

import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.fragments.list.albums.pictures.OnMediaDataNotify;
import com.ss.gallerypro.fragments.list.albums.pictures.model.IMediaRepository;
import com.ss.gallerypro.fragments.list.albums.pictures.view.IMediaView;

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
