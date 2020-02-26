package com.tunm.gallerypro.fragments.list.section.video;

import com.tunm.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.tunm.gallerypro.fragments.list.section.abstraction.presenter.BaseTimelinePresenter;
import com.tunm.gallerypro.fragments.list.section.abstraction.view.ITimelineView;

public class VideoPresenter extends BaseTimelinePresenter {

    public VideoPresenter(ITimelineView mView, ITimelineRepository mModel) {
        super(mView, mModel);
    }
}
