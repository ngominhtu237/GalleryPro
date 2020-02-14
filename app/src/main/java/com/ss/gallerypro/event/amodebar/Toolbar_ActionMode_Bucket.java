package com.ss.gallerypro.event.amodebar;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.ss.gallerypro.R;
import com.ss.gallerypro.fragments.list.normal.albums.AlbumsAdapter;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.fragments.list.normal.albums.AlbumsFragment;

import java.util.ArrayList;

public class Toolbar_ActionMode_Bucket implements ActionMode.Callback {

    private Fragment mFragment;
    private Context mContext;
    private AlbumsAdapter albumsAdapter;
    private ArrayList<Bucket> mBucket;
    private int mItemChecked = 1;

    public Toolbar_ActionMode_Bucket(Fragment mFragment, Context mContext, AlbumsAdapter albumsAdapter, ArrayList<Bucket> mBucket) {
        this.mFragment = mFragment;
        this.mContext = mContext;
        this.albumsAdapter = albumsAdapter;
        this.mBucket = mBucket;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_mode_album_menu, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (mItemChecked == 1) {
            menu.setGroupVisible(R.id.one_selected_items, true);
            menu.setGroupVisible(R.id.multi_selected_items, false);
        } else {
            menu.setGroupVisible(R.id.multi_selected_items, true);
            menu.setGroupVisible(R.id.one_selected_items, false);
        }
        if(mItemChecked == mBucket.size()) {
            menu.setGroupVisible(R.id.clear_select_group, true);
            menu.setGroupVisible(R.id.select_all_group, false);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AlbumsFragment albumsFragment = (AlbumsFragment) mFragment;
                if(albumsFragment != null) {
                    albumsFragment.deleteAlbums();
                }
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        albumsAdapter.removeSelection();
        AlbumsFragment albumsFragment = (AlbumsFragment) mFragment;

        albumsFragment.setNullToActionMode();
    }

    public void changeMenu(int numbItemCheck){
        this.mItemChecked = numbItemCheck;
        AlbumsFragment albumsFragment = (AlbumsFragment) mFragment;
        if(albumsFragment.getActionMode() != null){
            albumsFragment.getActionMode().invalidate();
        }
    }
}
