package com.ss.gallerypro.fragments.listHeader.abstraction.presenter;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.fragments.listHeader.abstraction.OnTimelineDataNotify;
import com.ss.gallerypro.fragments.listHeader.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.listHeader.abstraction.view.ITimelineView;

import java.util.ArrayList;

public abstract class BaseTimelinePresenter implements ITimelinePresenter  {
    protected ITimelineView mView;
    protected ITimelineRepository mModel;

    public BaseTimelinePresenter(ITimelineView mView, ITimelineRepository mModel) {
        this.mView = mView;
        this.mModel = mModel;
    }

    @Override
    public void getMedias(MediaFilter mediaFilter) {
        mModel.getDataTimeline(mediaFilter, getCallback);
    }

    @Override
    public void deleteMedias(ArrayList<MediaItem> mediaItems) {

    }


    protected final OnTimelineDataNotify.Get getCallback = new OnTimelineDataNotify.Get() {
        @Override
        public void onResponse(ArrayList<MediaItem> mediaItems) {
            mView.onGetTimelineSuccess(mediaItems);
        }

        @Override
        public void onError(String errMsg) {

        }
    };

    protected final OnTimelineDataNotify.Delete deleteCallback = new OnTimelineDataNotify.Delete() {
        @Override
        public void onResponse() {
            mView.onDeleteTimelineSuccess();
        }

        @Override
        public void onError(String errMsg) {

        }
    };
}
