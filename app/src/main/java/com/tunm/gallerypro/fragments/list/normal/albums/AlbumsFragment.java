package com.tunm.gallerypro.fragments.list.normal.albums;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tunm.gallerypro.CustomModelClass;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.customComponent.GridlayoutManagerFixed;
import com.tunm.gallerypro.data.AlbumHelper;
import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.data.LayoutType;
import com.tunm.gallerypro.data.filter.MediaFilter;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.event.amodebar.Toolbar_ActionMode_Bucket;
import com.tunm.gallerypro.fragments.RecycleViewClickListener;
import com.tunm.gallerypro.fragments.list.normal.abstraction.BaseListFragment;
import com.tunm.gallerypro.fragments.list.normal.abstraction.BaseListViewAdapter;
import com.tunm.gallerypro.fragments.list.normal.albums.model.AlbumRepositoryImpl;
import com.tunm.gallerypro.fragments.list.normal.albums.presenter.AlbumsPresenterImpl;
import com.tunm.gallerypro.fragments.list.normal.albums.view.IAlbumsView;
import com.tunm.gallerypro.fragments.list.split.pictures.AlbumPicturesFragment;
import com.tunm.gallerypro.setting.callback.ColumnChangeObserver;
import com.tunm.gallerypro.view.ItemOffsetDecoration;
import com.tunm.gallerypro.view.LockableViewPager;
import com.tunm.gallerypro.view.dialog.ChooseColumnDialog;
import com.tunm.gallerypro.view.dialog.DeleteDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class AlbumsFragment extends BaseListFragment implements IAlbumsView, RecycleViewClickListener, BaseListViewAdapter.CheckedItemInterface, ColumnChangeObserver {

    private static final String TAG = "AlbumsFragment";

    private AlbumsAdapter albumsAdapter;
    private Toolbar_ActionMode_Bucket toolbarActionModeBucket;
    private ArrayList<Bucket> receiveBuckets;
    private SparseBooleanArray selectedDeleteAlbum;

    private AlbumsPresenterImpl presenter;
    private LockableViewPager parentViewPager;

    private boolean isZoomIn, isScroll;

    @BindView(R.id.loading_layout)
    public RelativeLayout mLoadingLayout;

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
        CustomModelClass.getInstance().addColumnChangeObserver(this);
        Log.v("AlbumsFragment", "onCreate");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("AlbumsFragment", "onCreateView");
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (presenter.isListAlbumEmpty(receiveBuckets)) {
            loadData();
        } else {
            albumsAdapter.setDataList(receiveBuckets);
        }
        parentViewPager = (LockableViewPager) container;
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initRecycleView(View v) {
        super.initRecycleView(v);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnNumber = AlbumHelper.getNumbColumnPort(getActivity());
        } else {
            columnNumber = AlbumHelper.getNumbColumnLand(getActivity());
        }
        itemOffsetDecoration = new ItemOffsetDecoration(mAttachedActivity, R.dimen.timeline_item_spacing);
        albumsAdapter = new AlbumsAdapter(getActivity(), getSortingMode(), getSortingOrder(), mLayoutType);
        albumsAdapter.setItemCheckedInterface(this);
        if (mLayoutType == LayoutType.GRID) {
            mLayoutManager = new GridlayoutManagerFixed(getContext(), columnNumber);
        } else {
            mLayoutManager = new LinearLayoutManager(getContext());
        }
        mLayoutManager.setItemPrefetchEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_from_bottom);
        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.setItemAnimator(new LandingAnimator());
        mRecyclerView.setAdapter(albumsAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (mLayoutType == LayoutType.GRID) {
                            ((GridlayoutManagerFixed) mLayoutManager).setScrollEnabled(true);
                            isScroll = false;
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(mAttachedActivity, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (!isScroll && mLayoutType == LayoutType.GRID) {
                    float scaleFactor = detector.getScaleFactor();
                    isZoomIn = scaleFactor > 1;
                }
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                if (mLayoutType == LayoutType.GRID) {
                    ((GridlayoutManagerFixed) mLayoutManager).setScrollEnabled(false);
                    parentViewPager.setSwipeLocked(true);
                }
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if (mLayoutType == LayoutType.GRID) {
                    if (isZoomIn) {
                        if (columnNumber > ChooseColumnDialog.MIN_COLUMN_ALBUM) {
                            columnNumber--;
                            AlbumHelper.setNumbColumnPort(columnNumber);
                            animateRecyclerLayoutChange();
                        } else {
                            Toast.makeText(mAttachedActivity, "min column is: " + ChooseColumnDialog.MIN_COLUMN_ALBUM, Toast.LENGTH_SHORT).show();
                            ((GridlayoutManagerFixed) mLayoutManager).setScrollEnabled(true);
                            parentViewPager.setSwipeLocked(false);
                        }
                    } else {
                        if (columnNumber < ChooseColumnDialog.MAX_COLUMN_ALBUM) {
                            columnNumber++;
                            AlbumHelper.setNumbColumnPort(columnNumber);
                            animateRecyclerLayoutChange();
                        } else {
                            Toast.makeText(mAttachedActivity, "max column is: " + ChooseColumnDialog.MAX_COLUMN_ALBUM, Toast.LENGTH_SHORT).show();
                            ((GridlayoutManagerFixed) mLayoutManager).setScrollEnabled(false);
                            parentViewPager.setSwipeLocked(false);
                        }
                    }
                }
                super.onScaleEnd(detector);
            }
        });
        mRecyclerView.setOnTouchListener((view, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return false;
        });
    }

    protected void animateRecyclerLayoutChange() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(400);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((GridlayoutManagerFixed) mLayoutManager).setSpanCount(columnNumber);
                mLayoutManager.requestLayout();
                albumsAdapter.notifyDataSetChanged();

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(400);
                mRecyclerView.startAnimation(fadeIn);
                ((GridlayoutManagerFixed) mLayoutManager).setScrollEnabled(true);
                parentViewPager.setSwipeLocked(false);
            }
        });
        mRecyclerView.clearAnimation();
        mRecyclerView.startAnimation(fadeOut);
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
        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        AlbumPicturesFragment albumPicturesFragment = (AlbumPicturesFragment) fm.findFragmentByTag("AlbumPicturesFragment");
        if (albumPicturesFragment == null) {
            albumPicturesFragment = new AlbumPicturesFragment();
            Bundle args = new Bundle();
            args.putParcelable("album", albumsAdapter.getBuckets().get(position));
            albumPicturesFragment.setArguments(args);
        }
        FragmentTransaction f = fm.beginTransaction();
        f.add(R.id.fragment_container, albumPicturesFragment, "AlbumPicturesFragment");
        f.hide(AlbumsFragment.this); // => remove overlap optionMenu when add fragment

        // Add fragment one in back stack. So it will not be destroyed. Press back menu can pop it up from the stack.
        f.addToBackStack(AlbumsFragment.class.getName());
        f.commit();
    }

    @Override
    public void onLongClick(View view, int position) {
        onListItemSelect(position);
    }

    protected void onListItemSelect(int position) {
        albumsAdapter.toggleSelection(position);
        boolean hasCheckedItems = albumsAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            toolbarActionModeBucket = new Toolbar_ActionMode_Bucket(this, getActivity(), albumsAdapter, albumsAdapter.getBuckets());
            mActionMode = ((AppCompatActivity) Objects.requireNonNull(getActivity())).startSupportActionMode(toolbarActionModeBucket);
            parentFragment.hideAppBarLayout();
            parentViewPager.setSwipeLocked(true);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
            parentFragment.showAppBarLayout();
            parentViewPager.setSwipeLocked(false);
        }

        if (mActionMode != null) {
            mActionMode.setTitle(albumsAdapter.getSelectedCount() + " selected");
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
        if (mItemSelected == 0) {
            editMode = false;
        } else if (mItemSelected == 1) {
            oneSelected = true;
        }

        menu.setGroupVisible(R.id.general_album_items, !editMode);
        menu.setGroupVisible(R.id.view_mode, !editMode);
        menu.setGroupVisible(R.id.edit_mode_items, editMode);
        menu.setGroupVisible(R.id.one_selected_items, oneSelected);

        if (editMode) {

        } else {
            menu.findItem(R.id.ascending_sort_order).setChecked(getSortingOrder() == SortingOrder.ASCENDING);
            if (mLayoutType == LayoutType.GRID) {
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

            case R.id.filter_data:
                showMultiChoiceDialog();
                return true;

            case R.id.mode_grid:
                albumsAdapter.changeLayoutType(LayoutType.GRID);
                AlbumHelper.setLayoutType(LayoutType.GRID);
                mRecyclerView.removeItemDecoration(itemOffsetDecoration);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    columnNumber = AlbumHelper.getNumbColumnPort(getActivity());
                } else {
                    columnNumber = AlbumHelper.getNumbColumnLand(getActivity());
                }
                mLayoutManager = new GridlayoutManagerFixed(getContext(), columnNumber);
                mRecyclerView.setLayoutManager(new GridlayoutManagerFixed(getContext(), columnNumber));
                ((GridlayoutManagerFixed) mLayoutManager).setSpanCount(columnNumber);
                mRecyclerView.setAdapter(albumsAdapter);
                mLayoutType = getLayoutType();
                item.setChecked(true);
                return true;

            case R.id.mode_list:
                albumsAdapter.changeLayoutType(LayoutType.LIST);
                AlbumHelper.setLayoutType(LayoutType.LIST);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(albumsAdapter);
                mLayoutType = getLayoutType();
                item.setChecked(true);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void change(int numbItemCheck) {
        if (toolbarActionModeBucket != null) {
            toolbarActionModeBucket.changeMenu(numbItemCheck);
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteAlbums() {
        selectedDeleteAlbum = albumsAdapter.getSelectedIds(); // get all selected
        ArrayList<Bucket> mDeletedAlbums = new ArrayList<>();
        for (int i = (selectedDeleteAlbum.size() - 1); i >= 0; i--) {
            if (selectedDeleteAlbum.valueAt(i)) {
                mDeletedAlbums.add(albumsAdapter.getBuckets().get(selectedDeleteAlbum.keyAt(i)));
            }
        }
        DeleteDialog dialog = new DeleteDialog(mAttachedActivity);
        dialog.setTitle("Delete");
        String s = mDeletedAlbums.size() == 1 ? "album" : "albums";
        dialog.setMessage("Are you sure you want to delete " + mDeletedAlbums.size() + " " + s + "?");
        dialog.setNegativeButton("Cancel", v -> dialog.dismiss());
        dialog.setPositiveButton("Delete", v -> {
            dialog.dismiss();
            presenter.deleteAlbums(mDeletedAlbums);
            mActionMode.finish();
        });
        dialog.show();
    }

    public void loadData() {
        if(mLoadingLayout != null) {
            mLoadingLayout.setVisibility(View.VISIBLE);
        }
        if(mRecyclerView != null) {
            mRecyclerView.setVisibility(View.GONE);
        }
        presenter.getAlbums();
        albumsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetAlbumSuccess(ArrayList<Bucket> buckets) {
        if(buckets.size() > 0) {
            albumsAdapter.setDataList(buckets);
            albumsAdapter.changeSortingMode(getSortingMode());
            if(mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
            if(desertPlaceholder != null) desertPlaceholder.setVisibility(View.GONE);
            if(mRecyclerView != null) mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            if(desertPlaceholder != null) desertPlaceholder.setVisibility(View.VISIBLE);
            if(mRecyclerView != null) mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeleteAlbumSuccess() {
        for (int i = selectedDeleteAlbum.size() - 1; i >= 0; i--) {
            removeAlbum(selectedDeleteAlbum.keyAt(i));
        }
    }

    private void removeAlbum(int position) {
        albumsAdapter.removeAlbum(position);
    }

    @Override
    public void onChange() {
        Log.v("loadData", "update " + TAG);
        if (!getUserVisibleHint()) {
            loadData();
        }
    }

    @Override
    public void requestUpdateColumn() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnNumber = AlbumHelper.getNumbColumnPort(getActivity());
        } else {
            columnNumber = AlbumHelper.getNumbColumnLand(getActivity());
        }
        setupColumn();
        Log.v("update column", "AlbumsFragment");
    }

    // must override only here, not in BaseListFragment because we only need in AlbumsFragment => fix issue when back from PhotoSplitView
    @Override
    public void setNullToActionMode() {
        parentFragment.showAppBarLayout();
        parentViewPager.setSwipeLocked(false);
        super.setNullToActionMode();
    }

    @Override
    protected void refreshTheme() {
        super.refreshTheme();
        mLoadingLayout.setBackgroundColor(getResources().getColor(mColorTheme.isDarkTheme() ? R.color.colorDarkBackground : R.color.colorBackground));
    }

    private void showMultiChoiceDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),
                mColorTheme.isDarkTheme() ? R.style.AlbumsFilterDialogDark : R.style.AlbumsFilterDialogLight);
        String titleText = "Filter";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mColorTheme.isDarkTheme() ? getContext().getColor(R.color.colorDarkAccent) : mColorTheme.getPrimaryColor());
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
        // Apply the text color span
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        mBuilder.setTitle(ssBuilder);
        int n = MediaFilter.values().length;
        boolean[] checkedItem = new boolean[n];

        // get from pref => save to checkedItem (set("0", "1") -> index(0, 1) -> boolean in 'index' position)
        List<String> filter = new ArrayList<>(AlbumHelper.getFilter(n));
        for (int i = 0; i < filter.size(); i++) {
            checkedItem[Integer.parseInt(filter.get(i))] = true;
        }
        boolean[] newCheckedItem = Arrays.copyOf(checkedItem, n);

        mBuilder.setMultiChoiceItems(MediaFilter.getNames(), newCheckedItem,
                (dialogInterface, position, checked) -> newCheckedItem[position] = checked).setPositiveButton(R.string.ok_action,
                (dialogInterface, position) -> {
                    if (!Arrays.equals(checkedItem, newCheckedItem)) {
                        Set<String> newFilter = new HashSet<>();
                        for (int i = 0; i < n; i++) {
                            if (newCheckedItem[i]) newFilter.add(String.valueOf(i));
                        }
                        AlbumHelper.setFilter(newFilter);
                        if (newFilter.size() != 0) {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            desertPlaceholder.setVisibility(View.GONE);
                            loadData();
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            desertPlaceholder.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.nothing_changed, Toast.LENGTH_SHORT).show();
                    }

                }).setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
        addStyleFilterDialog(mDialog);
    }

    private void addStyleFilterDialog(AlertDialog dialog) {
        int w = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        Objects.requireNonNull(dialog.getWindow()).setLayout(w, ViewGroup.LayoutParams.WRAP_CONTENT);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(mColorTheme.isDarkTheme() ? getContext().getColor(R.color.colorDarkPrimary) : mColorTheme.getBackgroundColor());
        gd.setCornerRadius(25);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(gd);

        int colorAccent = mColorTheme.isDarkTheme() ? getContext().getColor(R.color.colorDarkAccent) : mColorTheme.getPrimaryColor();
        Button btNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btNegative.setTextColor(colorAccent);
        Button btPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btPositive.setTextColor(colorAccent);
    }
}
