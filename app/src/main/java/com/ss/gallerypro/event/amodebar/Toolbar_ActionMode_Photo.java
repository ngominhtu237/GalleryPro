package com.ss.gallerypro.event.amodebar;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.fragments.list.albums.pictures.AlbumPictureViewAdapter;
import com.ss.gallerypro.fragments.list.albums.pictures.AlbumPicturesFragment;

import java.util.ArrayList;

public class Toolbar_ActionMode_Photo implements ActionMode.Callback {

    private Context mContext;
    private ArrayList<MediaItem> mediaItems;
    private AlbumPictureViewAdapter adapter;
    private Fragment mFragment;
    private int mItemChecked = 1;

    public Toolbar_ActionMode_Photo(Fragment fragment, Context mContext, ArrayList<MediaItem> mediaItems, AlbumPictureViewAdapter adapter) {
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
                AlbumPicturesFragment albumPicturesFragment = (AlbumPicturesFragment) mFragment;
                if(albumPicturesFragment != null) {
                    albumPicturesFragment.deleteMedias();
                }
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.removeSelection();
        AlbumPicturesFragment albumPicturesFragment = (AlbumPicturesFragment) mFragment;
        albumPicturesFragment.setNullToActionMode();
    }

    public void changeMenu(int numbItemCheck){
        this.mItemChecked = numbItemCheck;
        AlbumPicturesFragment albumPicturesFragment = (AlbumPicturesFragment) mFragment;
        if(albumPicturesFragment.getActionMode() != null){
            albumPicturesFragment.getActionMode().invalidate();
        }
    }
}
