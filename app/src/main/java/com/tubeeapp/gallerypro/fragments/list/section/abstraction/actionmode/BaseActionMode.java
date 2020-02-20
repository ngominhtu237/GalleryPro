package com.tubeeapp.gallerypro.fragments.list.section.abstraction.actionmode;

import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.tubeeapp.gallerypro.fragments.list.section.abstraction.BaseTimelineFragment;

public abstract class BaseActionMode implements ActionMode.Callback {
    protected Fragment mFragment;
    private int mItemChecked = 1;

    public BaseActionMode(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(getActionModeLayout(), menu);
        return true;
    }

    protected abstract int getActionModeLayout();

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        BaseTimelineFragment fragment = (BaseTimelineFragment) mFragment;
        fragment.getAdapter().removeSelection();
        fragment.setNullToActionMode();
    }

    public void changeMenu(int numberItem){
        this.mItemChecked = numberItem;
        BaseTimelineFragment baseTimelineFragment = (BaseTimelineFragment) mFragment;
        if(baseTimelineFragment.getActionMode() != null){
            baseTimelineFragment.getActionMode().invalidate();
        }
    }
}
