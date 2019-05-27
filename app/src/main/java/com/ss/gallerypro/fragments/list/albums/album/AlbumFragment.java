package com.ss.gallerypro.fragments.list.albums.album;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.AlbumHelper;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.LayoutType;
import com.ss.gallerypro.data.filter.AlbumFilter;
import com.ss.gallerypro.data.provider.AlbumDataHandler;
import com.ss.gallerypro.data.provider.IAlbumDataChangedCallback;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.event.amodebar.Toolbar_ActionMode_Bucket;
import com.ss.gallerypro.fragments.RecycleViewClickListener;
import com.ss.gallerypro.fragments.list.abstraction.BaseListFragment;
import com.ss.gallerypro.fragments.list.abstraction.BaseListViewAdapter;
import com.ss.gallerypro.fragments.list.albums.pictures.AlbumPicturesFragment;
import com.ss.gallerypro.utils.Measure;
import com.ss.gallerypro.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class AlbumFragment extends BaseListFragment implements BaseListViewAdapter.CheckedItemInterface, IAlbumDataChangedCallback, OnNotifyDataChanged {

    static final int REQUEST_CODE = 1;
    private ArrayList<Bucket> mBuckets;
    private AlbumAdapter albumAdapter;
    private Toolbar_ActionMode_Bucket toolbarActionModeBucket;
    private ArrayList<Bucket> receiveBuckets;
    private SparseBooleanArray selectedDeleteAlbum;
    private AlbumDataHandler mAlbumDataHandler;

    public AlbumFragment() {
        super();
        mBuckets = new ArrayList<>();
        receiveBuckets = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbumDataHandler = new AlbumDataHandler(getContext());
        mAlbumDataHandler.setDataChangedCallback(this);

        // receive from SplashScreen
        receiveBuckets = getActivity().getIntent().getParcelableArrayListExtra("album_data");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);
        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
        desertPlaceholder = v.findViewById(R.id.placeholder);
        mSwipeRefreshListener = () -> {
            mAlbumDataHandler.getAlbums();
            albumAdapter.notifyDataSetChanged();
        };

        mSwipeRefreshLayout.setOnRefreshListener(mSwipeRefreshListener);
        if(receiveBuckets == null || receiveBuckets.size() == 0) {
            mSwipeRefreshLayout.post(() -> {
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshListener.onRefresh();
            });
        } else {
            mBuckets = receiveBuckets;
        }
        return v;
    }

    @Override
    protected void implementRecyclerViewClickListeners() {
        albumAdapter.setRecycleViewClickListener(new RecycleViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (mActionMode != null) {
                    onListItemSelect(position);
                } else {
                    handleClickItem(position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                setEnableSwipeRefresh(false);
                onListItemSelect(position);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album_view;
    }

    @SuppressLint("CommitTransaction")
    @Override
    protected void handleClickItem(int position) {

        // start AlbumPictureFragment
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        AlbumPicturesFragment albumPicturesFragment = (AlbumPicturesFragment) fragmentManager.findFragmentByTag("AlbumPicturesFragment");
        if(albumPicturesFragment == null) {
            albumPicturesFragment = new AlbumPicturesFragment();
            Bundle args = new Bundle();
            args.putParcelable("album", mBuckets.get(position));
            albumPicturesFragment.setArguments(args);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, albumPicturesFragment, "AlbumPicturesFragment");

        // Add fragment one in back stack. So it will not be destroyed. Press back menu can pop it up from the stack.
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    protected void onListItemSelect(int position) {
        albumAdapter.toggleSelection(position);

        boolean hasCheckedItems = albumAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            toolbarActionModeBucket = new Toolbar_ActionMode_Bucket(this, getActivity(), albumAdapter, mBuckets);
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(toolbarActionModeBucket);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
        }

        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(albumAdapter
                    .getSelectedCount()) + " selected");
        }

    }

    @Override
    protected void initRecycleView(View v) {
        super.initRecycleView(v);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            NUM_COLUMNS = AlbumHelper.getNumbColumnPort(getActivity());
        } else {
            NUM_COLUMNS = AlbumHelper.getNumbColumnLand(getActivity());
        }
        mGridSpacingItemDecoration = new GridSpacingItemDecoration(NUM_COLUMNS, Measure.pxToDp(2, getContext()), true);
        recyclerView.setHasFixedSize(true);
        albumAdapter = new AlbumAdapter(getActivity(), getSortingMode(), getSortingOrder(), mLayoutType, mBuckets);
        albumAdapter.setItemCheckedInterface(this);
        albumAdapter.setDataAdapterChangeCallback(this);
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
        recyclerView.setAdapter(albumAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.album_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public LayoutType getLayoutType() {
        return albumAdapter != null ? albumAdapter.getLayoutType() : AlbumHelper.getLayoutType();
    }

    @Override
    public SortingMode getSortingMode() {
        return albumAdapter != null ? albumAdapter.getSortingMode() : AlbumHelper.getSortingMode();
    }

    @Override
    public SortingOrder getSortingOrder() {
        return albumAdapter != null ? albumAdapter.getSortingOrder() : AlbumHelper.getSortingOrder();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean editMode = false, oneSelected = false;
        int mItemSelected = albumAdapter.getSelectedCount();
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
                case DATE:
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
                albumAdapter.changeSortingMode(SortingMode.NAME);
                AlbumHelper.setSortingMode(SortingMode.NAME);
                item.setChecked(true);
                return true;

            case R.id.size_sort_mode:
                albumAdapter.changeSortingMode(SortingMode.SIZE);
                AlbumHelper.setSortingMode(SortingMode.SIZE);
                item.setChecked(true);
                return true;

            case R.id.date_taken_sort_mode:
                albumAdapter.changeSortingMode(SortingMode.DATE);
                AlbumHelper.setSortingMode(SortingMode.DATE);
                item.setChecked(true);
                return true;

            case R.id.ascending_sort_order:
                item.setChecked(!item.isChecked());
                SortingOrder sortingOrder = SortingOrder.fromValue(item.isChecked());
                albumAdapter.changeSortingOrder(sortingOrder);
                AlbumHelper.setSortingOrder(sortingOrder);
                return true;

            case R.id.action_camera:
                startActivity(new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA));
                return true;

            case R.id.increase_column_count:
                if(NUM_COLUMNS+1 <= 10) {
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
                albumAdapter.changeLayoutType(LayoutType.GRID);
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
                recyclerView.setAdapter(albumAdapter);
                mLayoutType = getLayoutType();
                item.setChecked(true);
                return true;

            case R.id.mode_list:
                albumAdapter.changeLayoutType(LayoutType.LIST);
                AlbumHelper.setLayoutType(LayoutType.LIST);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(albumAdapter);
                mLayoutType = getLayoutType();
                item.setChecked(true);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showMultiChoiceDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle(R.string.title_dialog_album_filter);
        int n = AlbumFilter.values().length;
        boolean[] checkedItem = new boolean[n];

        // get from pref => save to checkedItem (set("0", "1") -> index(0, 1) -> boolean in 'index' position)
        List<String> filter = new ArrayList<>(AlbumHelper.getFilter(n));
        for(int i=0; i<filter.size(); i++){
            checkedItem[Integer.parseInt(filter.get(i))] = true;
        }
        boolean[] newCheckedItem = Arrays.copyOf(checkedItem, n);

        mBuilder.setMultiChoiceItems(AlbumFilter.getNames(), newCheckedItem, (dialogInterface, position, checked) -> {
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
                        mSwipeRefreshListener.onRefresh();
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

    @Override
    public void processGetDataFinish(ArrayList<Bucket> newBucket) {
        albumAdapter.updateData(newBucket);

        // se set lai sau
        albumAdapter.changeSortingMode(getSortingMode());
        mSwipeRefreshLayout.setRefreshing(false);
        if(newBucket.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            desertPlaceholder.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            desertPlaceholder.setVisibility(View.GONE);
        }
    }

    @Override
    public void processDeleteFinish() {
        for(int i=selectedDeleteAlbum.size()-1; i>=0; i--) {
            removeAlbum(selectedDeleteAlbum.keyAt(i));
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteAlbums(){
        selectedDeleteAlbum = albumAdapter.getSelectedIds(); // get all selected
        ArrayList<Bucket> mDeletedAlbums = new ArrayList<>();
        // Loop all selected ids
        for (int i = (selectedDeleteAlbum.size() - 1); i >= 0; i--) {
            if (selectedDeleteAlbum.valueAt(i)) {
                //If current id is selected remove the item via key
                mDeletedAlbums.add(mBuckets.get(selectedDeleteAlbum.keyAt(i)));
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
            mAlbumDataHandler.deleteAlbums(mDeletedAlbums);
            mActionMode.finish();
        });

    }

    @Override
    public void updateDataToView(ArrayList<Bucket> newBucket) {
        mBuckets = newBucket;
    }

    private void removeAlbum(int position) {
        albumAdapter.removeAlbum(position);
    }
}
