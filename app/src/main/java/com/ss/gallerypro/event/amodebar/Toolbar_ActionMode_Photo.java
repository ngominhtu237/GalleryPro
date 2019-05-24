package com.ss.gallerypro.event.amodebar;

import android.app.Activity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.ss.gallerypro.R;
import com.ss.gallerypro.activity.PhotoSplitView;
import com.ss.gallerypro.adapter.PhotoSplitViewAdapter;
import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public class Toolbar_ActionMode_Photo implements ActionMode.Callback {

    private Activity mActivity;
    private ArrayList<MediaItem> mediaItems;
    private PhotoSplitViewAdapter photoSplitViewAdapter;
    private PhotoSplitView photoSplitView;

    public Toolbar_ActionMode_Photo(Activity mActivity, ArrayList<MediaItem> mediaItems, PhotoSplitViewAdapter photoSplitViewAdapter) {
        this.mActivity = mActivity;
        this.mediaItems = mediaItems;
        this.photoSplitViewAdapter = photoSplitViewAdapter;
        photoSplitView = (PhotoSplitView) mActivity;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_mode_photo_menu, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if(photoSplitView != null) {
                    photoSplitView.deleteImages();
                }
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        photoSplitViewAdapter.removeSelection();
        photoSplitView.setNullToActionMode();
    }
}
