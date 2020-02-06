package com.ss.gallerypro.fragments.list.normal.abstraction;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.ss.gallerypro.DrawerLocker;
import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.LayoutType;
import com.ss.gallerypro.data.provider.ContentProviderObserver;
import com.ss.gallerypro.data.provider.ProviderChangeListener;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.BaseFragment;
import com.ss.gallerypro.fragments.home.HomeFragment;
import com.ss.gallerypro.utils.CommonMenuBarColor;
import com.ss.gallerypro.utils.Measure;
import com.ss.gallerypro.view.AnimatedRecyclerView;
import com.ss.gallerypro.view.GridSpacingItemDecoration;

import butterknife.BindView;

abstract public class BaseListFragment extends BaseFragment implements ProviderChangeListener {

    @Nullable
    @BindView(R.id.activity_main_swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.placeholder)
    protected DesertPlaceholder desertPlaceholder;

    @BindView(R.id.albumRecycleView)
    protected AnimatedRecyclerView recyclerView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected GridSpacingItemDecoration mGridSpacingItemDecoration;

    protected LayoutType mLayoutType;
    protected int NUM_COLUMNS;
    protected ActionMode mActionMode;
    protected Activity mAttachedActivity;

    private ContentProviderObserver mProviderObserver;

    protected HomeFragment parentFragment;

    public BaseListFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAttachedActivity = getActivity();
        parentFragment = ((HomeFragment) this.getParentFragment());

        mProviderObserver = new ContentProviderObserver();
        mProviderObserver.setChangeListener(this);
        mAttachedActivity.getContentResolver().
                registerContentObserver(
                        MediaStore.Files.getContentUri("external"),
                        true,
                        mProviderObserver);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initRecycleView(view);
        implementRecyclerViewClickListeners();
        ((DrawerLocker) mAttachedActivity).setDrawerEnabled(true);
        return view;
    }

    protected abstract void implementRecyclerViewClickListeners();

    @Override
    public void onResume() {
        if(mLayoutType == LayoutType.GRID) {
            setupColumn();
        }
        super.onResume();
        refreshTheme();
    }

    private void refreshTheme() {
        if(mColorTheme.isDarkTheme()) {
            recyclerView.setBackgroundColor(mAttachedActivity.getColor(R.color.colorDarkBackground));
            CommonMenuBarColor.setStatusBarColor(getActivity(), getActivity().getColor(R.color.colorDarkBackgroundHighlight));
        } else {
            recyclerView.setBackgroundColor(mColorTheme.getBackgroundColor());
            CommonMenuBarColor.setStatusBarColor(getActivity(), mColorTheme.getPrimaryColor());
        }
    }

    public void setupColumn() {
        if (NUM_COLUMNS != ((GridLayoutManager) mLayoutManager).getSpanCount()) {
            recyclerView.removeItemDecoration(mGridSpacingItemDecoration);
            mGridSpacingItemDecoration = new GridSpacingItemDecoration(NUM_COLUMNS, Measure.pxToDp(2, getContext()), true);
            recyclerView.addItemDecoration(mGridSpacingItemDecoration);
            recyclerView.setLayoutManager(new GridlayoutManagerFixed(getContext(), NUM_COLUMNS));
            ((GridlayoutManagerFixed) mLayoutManager).setSpanCount(NUM_COLUMNS);
        }
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    protected void initRecycleView(View v) {
        mLayoutType = getLayoutType();
        recyclerView.setHasFixedSize(true);
    }

    //Set enable SwipeRefreshLayout
    public void setEnableSwipeRefresh(boolean isEnable) {
        if(isEnable) {
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setDistanceToTriggerSync(0);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setDistanceToTriggerSync(999999);
        }
    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // override is optional
    protected LayoutType getLayoutType() {
        return null;
    }

    protected SortingMode getSortingMode() {
        return null;
    }

    protected SortingOrder getSortingOrder() {
        return null;
    }
}
