package com.tunm.gallerypro.fragments.list.split.pictures;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tunm.gallerypro.DeleteMediaItemSubject;
import com.tunm.gallerypro.DrawerLocker;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.customComponent.GridlayoutManagerFixed;
import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.event.amodebar.Toolbar_ActionMode_Photo;
import com.tunm.gallerypro.fragments.RecycleViewClickListener;
import com.tunm.gallerypro.fragments.list.normal.abstraction.BaseListFragment;
import com.tunm.gallerypro.fragments.list.normal.abstraction.BaseListViewAdapter;
import com.tunm.gallerypro.fragments.list.split.pictures.model.MediaRepositoryImpl;
import com.tunm.gallerypro.fragments.list.split.pictures.presenter.IMediaPresenter;
import com.tunm.gallerypro.fragments.list.split.pictures.presenter.MediaPresenterImpl;
import com.tunm.gallerypro.fragments.list.split.pictures.view.IMediaView;
import com.tunm.gallerypro.fragments.viewer.DeletedItemCallback;
import com.tunm.gallerypro.fragments.viewer.ImagePagerFragment;
import com.tunm.gallerypro.utils.ViewUtils;
import com.tunm.gallerypro.view.SquareImageView;
import com.tunm.gallerypro.view.dialog.AlbumPictureDetailsDialog;
import com.tunm.gallerypro.view.dialog.DeleteDialog;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class AlbumPicturesFragment extends BaseListFragment implements IMediaView, RecycleViewClickListener,
        BaseListViewAdapter.CheckedItemInterface, DeletedItemCallback {

    private static final String TAG = "AlbumPicturesFragment";

    private AlbumPictureViewAdapter adapter;
    private Toolbar_ActionMode_Photo toolbarActionModePhoto;
    private ArrayList<Integer> mListDeletedPosition = new ArrayList<>();

    private Bucket mReceiveBucket;
    private IMediaPresenter presenter;

    public AlbumPicturesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MediaPresenterImpl(this, new MediaRepositoryImpl(mActivity));

        Bundle bundle = getArguments();
        if(bundle != null) {
            mReceiveBucket = bundle.getParcelable("album");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mReceiveBucket != null) {
            Objects.requireNonNull(mActivity.getSupportActionBar()).setTitle(mReceiveBucket.getName());
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ((DrawerLocker) Objects.requireNonNull(mActivity)).setDrawerEnabled(false);

        // back from another fragment not call onCreate Fragment
        presenter.getMedias(mReceiveBucket);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin + ViewUtils.getStatusBarHeight(mActivity), params.rightMargin, params.bottomMargin);
        rootView.setLayoutParams(params);
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    public void deleteMedias() {
        ArrayList<MediaItem> mDeletedMedias = new ArrayList<>();
        mListDeletedPosition.clear();
        // Loop all selected ids
        for (int i = (adapter.getSelectedIds().size() - 1); i >= 0; i--) {
            if (adapter.getSelectedIds().valueAt(i)) {
                //If current id is selected remove the item via key
                mDeletedMedias.add(adapter.getMediaList().get(adapter.getSelectedIds().keyAt(i)));
                mListDeletedPosition.add(adapter.getSelectedIds().keyAt(i));
            }
        }
        DeleteDialog dialog = new DeleteDialog(mActivity);
        dialog.setTitle("Delete");
        String s = mListDeletedPosition.size() == 1 ? "item" : "items";
        dialog.setMessage("Are you sure you want to delete " + mListDeletedPosition.size() + " " + s + "?");
        dialog.setNegativeButton("Cancel", v -> dialog.dismiss());
        dialog.setPositiveButton("Delete", v -> {
            dialog.dismiss();
            presenter.deleteMedias(mDeletedMedias);
            mActionMode.finish();
        });
        dialog.show();
    }

    protected void onListItemSelect(int position) {
        adapter.toggleSelection(position);

        boolean hasCheckedItems = adapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            toolbarActionModePhoto = new Toolbar_ActionMode_Photo(this, getContext(), adapter.getMediaList(), adapter);
            mActionMode = Objects.requireNonNull(mActivity).startSupportActionMode(toolbarActionModePhoto);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
        }

        if (mActionMode != null) {
            mActionMode.setTitle(adapter.getSelectedCount() + " selected");
        }
    }

    @Override
    protected void initRecycleView(View v) {
        super.initRecycleView(v);
        mRecyclerView.setItemAnimator(new LandingAnimator());
        int NUM_COLUMNS = 4;
        adapter = new AlbumPictureViewAdapter(getContext(), getSortingMode(), getSortingOrder());
        adapter.setItemCheckedInterface(this);
        GridlayoutManagerFixed gridlayoutManagerFixed = new GridlayoutManagerFixed(getContext(), NUM_COLUMNS);
        mRecyclerView.setLayoutManager(gridlayoutManagerFixed);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void implementRecyclerViewClickListeners() {
        adapter.setRecycleViewClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        if (mActionMode != null) {
            onListItemSelect(position);
        } else {
            ImagePagerFragment.mImageList = adapter.getMediaList();
            SquareImageView transitioningView = view.findViewById(R.id.ivTimelineThumbnail);
            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            ImagePagerFragment pagerFragment = (ImagePagerFragment) fragmentManager.findFragmentByTag("ImagePagerFragment");
            if(pagerFragment == null) {
                pagerFragment = new ImagePagerFragment();
                Bundle args = new Bundle();
                args.putInt("currentPosition", position);
                args.putBoolean("isImage", true);
                pagerFragment.setArguments(args);
                pagerFragment.setDeleteCallback(this);
            }

            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, pagerFragment, ImagePagerFragment.class.getSimpleName())
                    .hide(AlbumPicturesFragment.this)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onLongClick(View view, int position) {
        onListItemSelect(position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mActivity.getMenuInflater().inflate(R.menu.album_pictures_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected SortingMode getSortingMode() {
        return adapter != null ? adapter.getSortingMode() : SortingMode.DATE_TAKEN;
    }

    @Override
    protected SortingOrder getSortingOrder() {
        return adapter != null ? adapter.getSortingOrder() : SortingOrder.DESCENDING;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        switch (getSortingMode()) {
            case NAME:
                menu.findItem(R.id.name_sort_mode_photo).setChecked(true);
                break;
            case DATE_TAKEN:
                menu.findItem(R.id.date_taken_sort_mode_photo).setChecked(true);
                break;
            case SIZE:
            default:
                menu.findItem(R.id.size_sort_mode_photo).setChecked(true);
                break;
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.name_sort_mode_photo:
                adapter.changeSortingMode(SortingMode.NAME);
                item.setChecked(true);
                return true;

            case R.id.size_sort_mode_photo:
                adapter.changeSortingMode(SortingMode.SIZE);
                item.setChecked(true);
                return true;

            case R.id.date_taken_sort_mode_photo:
                adapter.changeSortingMode(SortingMode.DATE_TAKEN);
                item.setChecked(true);
                return true;

            case R.id.ascending_sort_order_photo:
                item.setChecked(!item.isChecked());
                SortingOrder sortingOrder = SortingOrder.fromValue(item.isChecked());
                adapter.changeSortingOrder(sortingOrder);
                return true;

            case R.id.album_details:
                AlbumPictureDetailsDialog dialog = new AlbumPictureDetailsDialog(mActivity);
                dialog.setCurrentAlbum(mReceiveBucket);
                dialog.setMediaList(adapter.getMediaList());
                dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album_pictures;
    }

    @Override
    public void change(int numbItemCheck) {
        if(toolbarActionModePhoto != null) {
            toolbarActionModePhoto.changeMenu(numbItemCheck);
        }
    }

    @Override
    public void onGetMediaSuccess(ArrayList<MediaItem> medias) {
        adapter.setDataList(medias);
        adapter.changeSortingMode(getSortingMode());
        adapter.notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onFileChanged() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @Override
    public void onDeleteMediaSuccess() {
        for(int i=0; i<mListDeletedPosition.size(); i++) {
            adapter.removeImage(mListDeletedPosition.get(i));
        }
        DeleteMediaItemSubject.getInstance().notifyDataChange();
    }

    @Override
    public void onDelete(ArrayList<Integer> pos, ArrayList<MediaItem> mediaItems) {
        if(pos.size() > 0) {
            mListDeletedPosition = pos;
            presenter.deleteMedias(mediaItems);
        }
    }

    @Override
    public void onDataChanged() {
        // nothing need to do here
    }
}
