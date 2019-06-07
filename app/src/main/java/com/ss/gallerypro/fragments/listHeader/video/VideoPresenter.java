package com.ss.gallerypro.fragments.listHeader.video;

import com.ss.gallerypro.fragments.listHeader.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.listHeader.abstraction.presenter.BaseTimelinePresenter;
import com.ss.gallerypro.fragments.listHeader.abstraction.view.ITimelineView;

public class VideoPresenter extends BaseTimelinePresenter {

    public VideoPresenter(ITimelineView mView, ITimelineRepository mModel) {
        super(mView, mModel);
    }
}
