package com.ss.gallerypro.fragments.list.section.video;

import android.support.v4.app.Fragment;

import com.ss.gallerypro.R;
import com.ss.gallerypro.fragments.list.section.abstraction.actionmode.BaseActionMode;

public class VideoActionMode extends BaseActionMode {
    VideoActionMode(Fragment mFragment) {
        super(mFragment);
    }

    @Override
    protected int getActionModeLayout() {
        return R.menu.action_mode_video_menu;
    }
}
