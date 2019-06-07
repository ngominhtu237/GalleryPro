package com.ss.gallerypro.fragments.list.normal.albums;


import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import com.ss.gallerypro.DrawerLocker;
import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.AlbumHelper;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.LayoutType;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.event.amodebar.Toolbar_ActionMode_Bucket;
import com.ss.gallerypro.fragments.RecycleViewClickListener;
import com.ss.gallerypro.fragments.list.normal.abstraction.BaseListFragment;
import com.ss.gallerypro.fragments.list.normal.abstraction.BaseListViewAdapter;
import com.ss.gallerypro.fragments.list.normal.albums.model.AlbumRepositoryImpl;
import com.ss.gallerypro.fragments.list.normal.albums.presenter.AlbumsPresenterImpl;
import com.ss.gallerypro.fragments.list.normal.albums.view.IAlbumsView;
import com.ss.gallerypro.fragments.list.split.pictures.AlbumPicturesFragment;
import com.ss.gallerypro.utils.Measure;
import com.ss.gallerypro.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class AlbumsFragment extends BaseListFragment implements IAlbumsView, RecycleViewClickListener, BaseListViewAdapter.CheckedItemInterface {

    private AlbumsAdapter albumsAdapter;
    private Toolbar_ActionMode_Bucket toolbarActionModeBucket;
    private ArrayList<Bucket> receiveBuckets;
    private SparseBooleanArray selectedDeleteAlbum;

    private AlbumsPresenterImpl presenter;

    public AlbumsFragment() {
        super();
        receiveBuckets = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AlbumsPresenterImpl(this, new AlbumRepositoryImpl(mAttachedActivity));

        // receive from SplashScreen
        receiveBuckets = mAttachedActivity.getIntent().getParcelableArrayListExtra("album_data");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((DrawerLocker) mAttachedActivity).setDrawerEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(listener);

        if(presenter.isListAlbumEmpty(receiveBuckets)) {
            mSwipeRefreshLayout.post(() -> {
                mSwipeRefreshLayout.setRefreshing(true);
                listener.onRefresh();
            });
        } else {
            albumsAdapter.setDataList(receiveBuckets);
        }
        return view;
    }

    @Override
    protected void initRecycleView(View v) {
        super.initRecycleView(v);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            NUM_COLUMNS = AlbumHelper.getNumbColumnPort(getActivity());
        } else {
            NUM_COLUMNS = AlbumHelper.getNumbColumnLand(getActivity());
        }
        mGridSpacingItemDecoration = new GridSpacingItemDecoration(NUM_COLUMNS, Measure.pxToDp(2, mAttachedActivity), true);
        albumsAdapter = new AlbumsAdapter(getActivity(), getSortingMode(), getSortingOrder(), mLayoutType);
        albumsAdapter.setItemCheckedInterface(this);
        if(mLayoutType == LayoutType.GRID) {
            recyclerView.addItemDecoration(mGridSpacingItemDecoration);
            mLayoutManager = new GridlayoutManagerFixed(getContext(), NUM_COLUMNS);
        } else {
            recyclerView.removeItemDecoration(mGridSpacingItemDecoration);
            mLayoutManager = new LinearLayoutManager(getContext());
        }
        recyclerView.setLayoutManager(mLayoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.setAdapter(albumsAdapter);
    }

    @Override
    protected void implementRecyclerViewClickListeners() {
        albumsAdapter.setRecycleViewClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        if (mActionMode != null) {
            onListItemSelect(position);
        } else {
            openAlbum(position);
        }
    }

    protected void openAlbum(int position) {
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        AlbumPicturesFragment albumPicturesFragment = (AlbumPicturesFragment) fragmentManager.findFragmentByTag("AlbumPicturesFragment");
        if(albumPicturesFragment == null) {
            albumPicturesFragment = new AlbumPicturesFragment();
            Bundle args = new Bundle();
            args.putParcelable("album", albumsAdapter.getBuckets().get(position));
            albumPicturesFragment.setArguments(args);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, albumPicturesFragment, "AlbumPicturesFragment");

        // Add fragment one in back stack. So it will not be destroyed. Press back menu can pop it up from the stack.
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onLongClick(View view, int position) {
        setEnableSwipeRefresh(false);
        onListItemSelect(position);
    }

    protected void onListItemSelect(int position) {
        albumsAdapter.toggleSelection(position);
        boolean hasCheckedItems = albumsAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            toolbarActionModeBucket = new Toolbar_ActionMode_Bucket(this, getActivity(), albumsAdapter, albumsAdapter.getBuckets());
            mActionMode = ((AppCompatActivity) Objects.requireNonNull(getActivity())).startSupportActionMode(toolbarActionModeBucket);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
        }

        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(albumsAdapter
                    .getSelectedCount()) + " selected");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album_view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mAttachedActivity.getMenuInflater().inflate(R.menu.album_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public LayoutType getLayoutType() {
        return albumsAdapter != null ? albumsAdapter.getLayoutType() : AlbumHelper.getLayoutType();
    }

    @Override
    public SortingMode getSortingMode() {
        return albumsAdapter != null ? albumsAdapter.getSortingMode() : AlbumHelper.getSortingMode();
    }

    @Override
    public SortingOrder getSortingOrder() {
        return albumsAdapter != null ? albumsAdapter.getSortingOrder() : AlbumHelper.getSortingOrder();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean editMode = false, oneSelected = false;
        int mItemSelected = albumsAdapter.getSelectedCount();
        if(mItemSelected == 0) {
            editMode = false;
        } else if (mItemSelected == 1) {
            oneSelected = true;
        }

        menu.setGroupVisible(R.id.general_album_items, !editMode);
        menu.setGroupVisible(R.id.view_mode, !editMode);
        menu.setGroupVisible(R.id.edit_mode_items, editMode);
        menu.setGroupVisible(R.id.one_selected_items, oneSelected);
        // not visible increase & reduce column when in list mode
        MenuItem item = menu.findItem(R.id.change_column);
        item.setVisible(mLayoutType == LayoutType.GRID);

        if (editMode) {

        } else {
            menu.findItem(R.id.ascending_sort_order).setChecked(getSortingOrder() == SortingOrder.ASCENDING);
            if(mLayoutType == LayoutType.GRID) {
                menu.findItem(R.id.mode_grid).setChecked(true);
            } else {
                menu.findItem(R.id.mode_list).setChecked(true);
            }
            switch (getSortingMode()) {
                case NAME:
                    menu.findItem(R.id.name_sort_mode).setChecked(true);
                    break;
                case DATE_TAKEN:
                    menu.findItem(R.id.date_taken_sort_mode).setChecked(true);
                    break;
                case SIZE:
                default:
                    menu.findItem(R.id.size_sort_mode).setChecked(true);
                    break;
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.name_sort_mode:
                albumsAdapter.changeSortingMode(SortingMode.NAME);
                AlbumHelper.setSortingMode(SortingMode.NAME);
                item.setChecked(true);
                return true;

            case R.id.size_sort_mode:
                albumsAdapter.changeSortingMode(SortingMode.SIZE);
                AlbumHelper.setSortingMode(SortingMode.SIZE);
                item.setChecked(true);
                return true;

            case R.id.date_taken_sort_mode:
                albumsAdapter.changeSortingMode(SortingMode.DATE_TAKEN);
                AlbumHelper.setSortingMode(SortingMode.DATE_TAKEN);
                item.setChecked(true);
                return true;

            case R.id.ascending_sort_order:
                item.setChecked(!item.isChecked());
                SortingOrder sortingOrder = SortingOrder.fromValue(item.isChecked());
                albumsAdapter.changeSortingOrder(sortingOrder);
                AlbumHelper.setSortingOrder(sortingOrder);
                return true;

            case R.id.action_camera:
                startActivity(new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA));
                return true;

            case R.id.increase_column_count:
                if(NUM_COLUMNS+1 <= 6) {
                    NUM_COLUMNS++;
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        AlbumHelper.setNumbColumnPort(NUM_COLUMNS);
                    } else {
                        AlbumHelper.setNumbColumnLand(NUM_COLUMNS);
                    }
                    setUpColumns();
                } else {
                    Toast.makeText(getContext(), "Max column is 10", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.reduce_column_count:
                if(NUM_COLUMNS-1 >= 1) {
                    NUM_COLUMNS--;
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        AlbumHelper.setNumbColumnPort(NUM_COLUMNS);
                    } else {
                        AlbumHelper.setNumbColumnLand(NUM_COLUMNS);
                    }
                    setUpColumns();
                } else {
                    Toast.makeText(getContext(), "Min column is 1", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.filter_data:
                showMultiChoiceDialog();
                return true;

            case R.id.mode_grid:
                albumsAdapter.changeLayoutType(LayoutType.GRID);
                AlbumHelper.setLayoutType(LayoutType.GRID);
                recyclerView.removeItemDecoration(mGridSpacingItemDecoration);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    NUM_COLUMNS = AlbumHelper.getNumbColumnPort(getActivity());
                } else {
                    NUM_COLUMNS = AlbumHelper.getNumbColumnLand(getActivity());
                }
                mLayoutManager = new GridlayoutManagerFixed(getContext(), NUM_COLUMNS);
                mGridSpacingItemDecoration = new GridSpacingItemDecoration(NUM_COLUMNS, Measure.pxToDp(2, getContext()), true);
                recyclerView.addItemDecoration(mGridSpacingItemDecoration);
                recyclerView.setLayoutManager(new GridlayoutManagerFixed(getContext(), NUM_COLUMNS));
                ((GridlayoutManagerFixed) mLayoutManager).setSpanCount(NUM_COLUMNS);
                recyclerView.setAdapter(albumsAdapter);
                mLayoutType = getLayoutType();
                item.setChecked(true);
                return true;

            case R.id.mode_list:
                albumsAdapter.changeLayoutType(LayoutType.LIST);
                AlbumHelper.setLayoutType(LayoutType.LIST);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(albumsAdapter);
                mLayoutType = getLayoutType();
                item.setChecked(true);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showMultiChoiceDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle(R.string.title_dialog_album_filter);
        int n = MediaFilter.values().length;
        boolean[] checkedItem = new boolean[n];

        // get from pref => save to checkedItem (set("0", "1") -> index(0, 1) -> boolean in 'index' position)
        List<String> filter = new ArrayList<>(AlbumHelper.getFilter(n));
        for(int i=0; i<filter.size(); i++){
            checkedItem[Integer.parseInt(filter.get(i))] = true;
        }
        boolean[] newCheckedItem = Arrays.copyOf(checkedItem, n);

        mBuilder.setMultiChoiceItems(MediaFilter.getNames(), newCheckedItem, (dialogInterface, position, checked) -> {
            newCheckedItem[position] = checked;
        }).setPositiveButton(R.string.ok_action, (dialogInterface, position) -> {
            if(!Arrays.equals(checkedItem, newCheckedItem)) {
                Set<String> newFilter = new HashSet<>();
                for (int i = 0; i < n; i++) {
                    if (newCheckedItem[i]) newFilter.add(String.valueOf(i));
                }
                AlbumHelper.setFilter(newFilter);
                if(newFilter.size() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    desertPlaceholder.setVisibility(View.GONE);
                    mSwipeRefreshLayout.post(() -> {
                        mSwipeRefreshLayout.setRefreshing(true);
                        listener.onRefresh();
                    });
                } else {
                    recyclerView.setVisibility(View.GONE);
                    desertPlaceholder.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getContext(), R.string.nothing_changed, Toast.LENGTH_SHORT).show();
            }

        }).setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    @Override
    public void change(int numbItemCheck) {
        if(toolbarActionModeBucket != null) {
            toolbarActionModeBucket.changeMenu(numbItemCheck);
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteAlbums(){
        selectedDeleteAlbum = albumsAdapter.getSelectedIds(); // get all selected
        ArrayList<Bucket> mDeletedAlbums = new ArrayList<>();
        for (int i = (selectedDeleteAlbum.size() - 1); i >= 0; i--) {
            if (selectedDeleteAlbum.valueAt(i)) {
                mDeletedAlbums.add(albumsAdapter.getBuckets().get(selectedDeleteAlbum.keyAt(i)));
            }
        }
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.dialog_custom);
        dialog.show();
        Button btCancel = dialog.findViewById(R.id.btn_cancel);
        Button btDelete = dialog.findViewById(R.id.btn_delete);
        btCancel.setOnClickListener(view -> dialog.dismiss());
        btDelete.setOnClickListener(view -> {
            dialog.dismiss();
            presenter.deleteAlbums(mDeletedAlbums);
            mActionMode.finish();
        });

    }

    private final SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            presenter.getAlbums();
            albumsAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onGetAlbumSuccess(ArrayList<Bucket> buckets) {
        albumsAdapter.setDataList(buckets);
        albumsAdapter.changeSortingMode(getSortingMode());
        mSwipeRefreshLayout.setRefreshing(false);
        if(presenter.isListAlbumEmpty(buckets)) {
            recyclerView.setVisibility(View.GONE);
            desertPlaceholder.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            desertPlaceholder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeleteAlbumSuccess() {
        for(int i=selectedDeleteAlbum.size()-1; i>=0; i--) {
            removeAlbum(selectedDeleteAlbum.keyAt(i));
        }
    }

    private void removeAlbum(int position) {
        albumsAdapter.removeAlbum(position);
    }
}
