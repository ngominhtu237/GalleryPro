package com.tubeeapp.gallerypro.event.amodebar;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.fragments.list.split.video.VideosAdapter;
import com.tubeeapp.gallerypro.fragments.list.split.video.VideosFragment;

import java.util.ArrayList;

public class Toolbar_ActionMode_Video implements ActionMode.Callback {
    private Context mContext;
    private ArrayList<MediaItem> mediaItems;
    private VideosAdapter adapter;
    private Fragment mFragment;
    private int mItemChecked = 1;

    public Toolbar_ActionMode_Video(Fragment fragment, Context mContext, ArrayList<MediaItem> mediaItems, VideosAdapter adapter) {
        this.mContext = mContext;
        this.mediaItems = mediaItems;
        this.adapter = adapter;
        mFragment = fragment;
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
                VideosFragment videosFragment = (VideosFragment) mFragment;
                if(videosFragment != null) {
                    videosFragment.deleteMedias();
                }
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.removeSelection();
        VideosFragment videosFragment = (VideosFragment) mFragment;
        videosFragment.setNullToActionMode();
    }

    public void changeMenu(int numbItemCheck){
        this.mItemChecked = numbItemCheck;
        VideosFragment videosFragment = (VideosFragment) mFragment;
        if(videosFragment.getActionMode() != null){
            videosFragment.getActionMode().invalidate();
        }
    }
}
