package com.ss.gallerypro.fragments.list.split.video.presenter;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.fragments.list.split.OnMediaDataNotify;
import com.ss.gallerypro.fragments.list.split.video.model.IVideoRepository;
import com.ss.gallerypro.fragments.list.split.video.view.IVideoView;

import java.util.ArrayList;

public class VideoPresenterImpl implements IVideoPresenter {

    private IVideoView mView;
    private IVideoRepository mRepository;

    public VideoPresenterImpl(IVideoView mView, IVideoRepository mRepository) {
        this.mView = mView;
        this.mRepository = mRepository;
    }

    @Override
    public void getVideoList() {
        mRepository.getVideoList(getVideoCallback);
    }

    @Override
    public void deleteMedias(ArrayList<MediaItem> medias) {
        mRepository.deleteVideos(medias, deleteVideoCallback);
    }

    private final OnMediaDataNotify.GetMedia getVideoCallback = new OnMediaDataNotify.GetMedia() {
        @Override
        public void onResponse(ArrayList<MediaItem> medias) {
            mView.onGetVideoSuccess(medias);
        }

        @Override
        public void onError(String errMsg) {

        }
    };

    private final OnMediaDataNotify.DeleteMedia deleteVideoCallback = new OnMediaDataNotify.DeleteMedia() {
        @Override
        public void onResponse() {
            mView.onDeleteVideoSuccess();
        }

        @Override
        public void onError(String errMsg) {

        }
    };
}
