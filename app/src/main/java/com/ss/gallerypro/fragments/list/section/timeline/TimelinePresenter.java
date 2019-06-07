package com.ss.gallerypro.fragments.list.section.timeline;

import com.ss.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.list.section.abstraction.presenter.BaseTimelinePresenter;
import com.ss.gallerypro.fragments.list.section.abstraction.view.ITimelineView;

public class TimelinePresenter extends BaseTimelinePresenter {

    TimelinePresenter(ITimelineView mView, ITimelineRepository mModel) {
        super(mView, mModel);
    }
}
