package com.tunm.gallerypro.fragments.list.section.abstraction.presenter;

import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.filter.MediaFilter;
import com.tunm.gallerypro.fragments.list.section.abstraction.OnTimelineDataNotify;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.tunm.gallerypro.fragments.list.section.abstraction.view.ITimelineView;

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
        mModel.deleteDataTimeline(mediaItems, deleteCallback);
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
