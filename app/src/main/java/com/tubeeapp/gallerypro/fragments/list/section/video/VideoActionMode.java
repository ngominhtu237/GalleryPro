package com.tubeeapp.gallerypro.fragments.list.section.video;

import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.fragments.list.section.abstraction.actionmode.BaseActionMode;

public class VideoActionMode extends BaseActionMode {
    VideoActionMode(Fragment mFragment) {
        super(mFragment);
    }

    @Override
    protected int getActionModeLayout() {
        return R.menu.action_mode_video_menu;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                VideoFragment fragment = (VideoFragment) mFragment;
                if(fragment != null) {
                    fragment.deleteMedias();
                }
                break;
        }
        return false;
    }
}
