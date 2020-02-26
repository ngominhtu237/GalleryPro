package com.tunm.gallerypro.fragments.list.section.timeline;

import com.tunm.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.tunm.gallerypro.fragments.list.section.abstraction.presenter.BaseTimelinePresenter;
import com.tunm.gallerypro.fragments.list.section.abstraction.view.ITimelineView;

public class TimelinePresenter extends BaseTimelinePresenter {

    TimelinePresenter(ITimelineView mView, ITimelineRepository mModel) {
        super(mView, mModel);
    }
}
