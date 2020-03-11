package com.tunm.gallerypro.fragments.list.split.pictures;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
import com.tunm.gallerypro.fragments.viewer.ImagePagerFragment;
import com.tunm.gallerypro.utils.ViewSizeUtils;
import com.tunm.gallerypro.view.SquareImageView;
import com.tunm.gallerypro.view.dialog.AlbumPictureDetailsDialog;
import com.tunm.gallerypro.view.dialog.DeleteDialog;

import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class AlbumPicturesFragment extends BaseListFragment implements IMediaView, RecycleViewClickListener, BaseListViewAdapter.CheckedItemInterface {

    private AlbumPictureViewAdapter adapter;
    private Toolbar_ActionMode_Photo toolbarActionModePhoto;
    private View rootView;
    private SparseBooleanArray selectedMediaDelete;

    private Bucket mReceiveBucket;
    private IMediaPresenter presenter;

    public AlbumPicturesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MediaPresenterImpl(this, new MediaRepositoryImpl(mAttachedActivity));

        Bundle bundle = getArguments();
        if(bundle != null) {
            mReceiveBucket = bundle.getParcelable("album");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mReceiveBucket != null) {
            Objects.requireNonNull(((AppCompatActivity) mAttachedActivity).getSupportActionBar()).setTitle(mReceiveBucket.getName());
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        ((DrawerLocker) Objects.requireNonNull(getActivity())).setDrawerEnabled(false);

        // back from another fragment not call onCreate Fragment
        presenter.getMedias(mReceiveBucket);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin + ViewSizeUtils.getStatusBarHeight(getActivity()), params.rightMargin, params.bottomMargin);
        rootView.setLayoutParams(params);
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    public void deleteMedias() {
        selectedMediaDelete = adapter.getSelectedIds();
        ArrayList<MediaItem> mDeletedMedias = new ArrayList<>();
        // Loop all selected ids
        for (int i = (selectedMediaDelete.size() - 1); i >= 0; i--) {
            if (selectedMediaDelete.valueAt(i)) {
                //If current id is selected remove the item via key
                mDeletedMedias.add(adapter.getMediaList().get(selectedMediaDelete.keyAt(i)));
            }
        }
        DeleteDialog dialog = new DeleteDialog(mAttachedActivity);
        dialog.setTitle("Delete");
        String s = selectedMediaDelete.size() == 1 ? "item" : "items";
        dialog.setMessage("Are you sure you want to delete " + selectedMediaDelete.size() + " " + s + "?");
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
            mActionMode = ((AppCompatActivity) Objects.requireNonNull(mAttachedActivity)).startSupportActionMode(toolbarActionModePhoto);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
        }

        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(adapter
                    .getSelectedCount()) + " selected");
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
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            ImagePagerFragment pagerFragment = (ImagePagerFragment) fragmentManager.findFragmentByTag("ImagePagerFragment");
            if(pagerFragment == null) {
                pagerFragment = new ImagePagerFragment();
                Bundle args = new Bundle();
                args.putInt("currentPosition", position);
                args.putBoolean("isImage", true);
                pagerFragment.setArguments(args);
                // pagerFragment.setDeleteCallback(this);
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
        mAttachedActivity.getMenuInflater().inflate(R.menu.album_pictures_menu, menu);
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
                AlbumPictureDetailsDialog dialog = new AlbumPictureDetailsDialog(mAttachedActivity);
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

    private void removeImage(int position) {
        adapter.removeImage(position);
    }

    @Override
    public void onGetMediaSuccess(ArrayList<MediaItem> medias) {
        adapter.setDataList(medias);
        adapter.changeSortingMode(getSortingMode());
        adapter.notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onDeleteMediaSuccess() {
        for(int i=selectedMediaDelete.size()-1; i>=0; i--) {
            removeImage(selectedMediaDelete.keyAt(i));
        }
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
}
