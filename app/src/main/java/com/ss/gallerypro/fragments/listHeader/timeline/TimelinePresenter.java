package com.ss.gallerypro.fragments.listHeader.timeline;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.fragments.listHeader.abstraction.OnTimelineDataNotify;
import com.ss.gallerypro.fragments.listHeader.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.listHeader.abstraction.presenter.ITimelinePresenter;
import com.ss.gallerypro.fragments.listHeader.abstraction.view.ITimelineView;

import java.util.ArrayList;

public class TimelinePresenter implements ITimelinePresenter {
    private ITimelineView mView;
    private ITimelineRepository mModel;

    TimelinePresenter(ITimelineView view, ITimelineRepository model) {
        this.mView = view;
        this.mModel = model;
    }

    @Override
    public void getMedias(MediaFilter mediaFilter) {
        mModel.getDataTimeline(mediaFilter, getCallback);
    }

    @Override
    public void deleteMedias(ArrayList<MediaItem> mediaItems) {

    }

    private final OnTimelineDataNotify.Get getCallback = new OnTimelineDataNotify.Get() {
        @Override
        public void onResponse(ArrayList<MediaItem> mediaItems) {
            mView.onGetTimelineSuccess(mediaItems);
        }

        @Override
        public void onError(String errMsg) {

        }
    };

    private final OnTimelineDataNotify.Delete deleteCallback = new OnTimelineDataNotify.Delete() {
        @Override
        public void onResponse() {
            mView.onDeleteTimelineSuccess();
        }

        @Override
        public void onError(String errMsg) {

        }
    };
}
