package com.ss.gallerypro.fragments.listHeader.abstraction;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.ss.gallerypro.DrawerLocker;
import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.fragments.BaseFragment;
import com.ss.gallerypro.utils.Measure;
import com.ss.gallerypro.view.GridSpacingItemDecoration;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public abstract class BaseHeaderFragment extends BaseFragment {
    @BindView(R.id.timelineRecycleView)
    protected RecyclerView mRecyclerView;

    @Nullable
    @BindView(R.id.activity_main_swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.placeholder)
    protected DesertPlaceholder desertPlaceholder;

    protected Activity mAttachedActivity;

    protected GridLayoutManager mLayoutManager;
    protected GridSpacingItemDecoration mGridSpacingItemDecoration;
    private ActionMode mActionMode;
    protected int NUM_COLUMN;

    public BaseHeaderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAttachedActivity = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initRecycleView();
        ((DrawerLocker) mAttachedActivity).setDrawerEnabled(false);
        return view;
    }

    private void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        NUM_COLUMN = getNumberColumn();
        mGridSpacingItemDecoration = new GridSpacingItemDecoration(NUM_COLUMN, Measure.pxToDp(1, mAttachedActivity), false);
        mRecyclerView.addItemDecoration(mGridSpacingItemDecoration);
        mLayoutManager = new GridlayoutManagerFixed(getContext(), NUM_COLUMN);
        setSpanCountItem();
        mRecyclerView.setLayoutManager(mLayoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.setItemAnimator(new LandingAnimator());
        initAndSetAdapter();
    }

    protected abstract void setSpanCountItem();

    protected abstract int getNumberColumn();

    protected abstract void initAndSetAdapter();

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public void setEnableSwipeRefresh(boolean isEnable) {
        if(isEnable) {
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setDistanceToTriggerSync(0);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setDistanceToTriggerSync(999999);
        }
    }

    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }
}
