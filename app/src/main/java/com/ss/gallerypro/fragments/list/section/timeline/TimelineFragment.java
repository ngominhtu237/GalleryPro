package com.ss.gallerypro.fragments.list.section.timeline;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.TimelineHelper;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.list.section.abstraction.BaseTimelineAdapter;
import com.ss.gallerypro.fragments.list.section.abstraction.BaseTimelineFragment;
import com.ss.gallerypro.fragments.list.section.abstraction.actionmode.BaseActionMode;
import com.ss.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.list.section.abstraction.presenter.ITimelinePresenter;
import com.ss.gallerypro.fragments.list.section.abstraction.view.ITimelineView;
import com.ss.gallerypro.fragments.viewer.ImagePagerFragment;
import com.ss.gallerypro.view.SquareImageView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends BaseTimelineFragment implements ITimelineView {

    private static final String TAG = "TimelineFragment";

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BaseActionMode createActionMode() {
        return new TimelineActionMode(this);
    }

    @Override
    protected ITimelineRepository createModel() {
        return new TimelineRepository(mAttachedActivity);
    }

    @Override
    protected ITimelinePresenter createPresenter(ITimelineRepository model) {
        return new TimelinePresenter(this, model);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(() -> {
                if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);
                listener.onRefresh();
                Log.v(TAG, "load data.");
            });
        }
        return view;
    }

    @Override
    protected int getContentColumn() {
        return 1;
    }

    @Override
    protected int getHeaderColumn() {
        return 3;
    }

    @Override
    protected BaseTimelineAdapter createAdapter() {
        return new TimelineAdapter(this, mAttachedActivity, getSortingMode(), getSortingOrder());
    }

    @Override
    protected int getColumnRecycleView() {
        return 3;
    }

    @Override
    protected void createSwipeEvent() {
        presenter.getMedias(MediaFilter.IMAGE);
    }

    @Override
    protected SortingMode getSortModeFromPref() {
        return TimelineHelper.getSortingMode();
    }

    @Override
    protected SortingOrder getSortOrderFromPref() {
        return TimelineHelper.getSortingOrder();
    }

    @Override
    protected void setSortModeToPref(SortingMode sortingMode) {
        TimelineHelper.setSortingMode(sortingMode);
    }

    @Override
    protected void setSortOrderToPref(SortingOrder sortingOrder) {
        TimelineHelper.setSortingOrder(sortingOrder);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_timeline;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mAttachedActivity.getMenuInflater().inflate(R.menu.timeline_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.general_timeline_items, true);
        menu.setGroupVisible(R.id.edit_mode_items, false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onGetTimelineSuccess(ArrayList<MediaItem> mediaItems) {
        getAdapter().setMediaItems(mediaItems);
        getAdapter().changeSorting(getSortingMode(), getSortingOrder());
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDeleteTimelineSuccess() {

    }

    @Override
    public void onClick(View view, int position) {
        if (mActionMode != null) {
            onListItemSelect(position);
        } else {
            ((TransitionSet) getExitTransition()).excludeTarget(view, true);

            ImagePagerFragment.mImageList = getAdapter().getMediaItems();
            SquareImageView transitioningView = view.findViewById(R.id.ivTimelineThumbnail);
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            ImagePagerFragment pagerFragment = (ImagePagerFragment) fragmentManager.findFragmentByTag("ImagePagerFragment");
            if(pagerFragment == null) {
                pagerFragment = new ImagePagerFragment();
                Bundle args = new Bundle();
                args.putInt("currentPosition", position);
                pagerFragment.setArguments(args);
            }

            fragmentManager
                    .beginTransaction()
                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
                    .add(R.id.fragment_container, pagerFragment, ImagePagerFragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onLongClick(View view, int position) {
        setEnableSwipeRefresh(false);
        onListItemSelect(position);
    }

    @Override
    public void onLoadCompleted() {
        startPostponedEnterTransition();
    }
}
