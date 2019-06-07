package com.ss.gallerypro.fragments.listHeader.timeline;

import com.ss.gallerypro.fragments.listHeader.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.listHeader.abstraction.presenter.BaseTimelinePresenter;
import com.ss.gallerypro.fragments.listHeader.abstraction.view.ITimelineView;

public class TimelinePresenter extends BaseTimelinePresenter {

    TimelinePresenter(ITimelineView mView, ITimelineRepository mModel) {
        super(mView, mModel);
    }
}
