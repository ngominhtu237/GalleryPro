package com.ss.gallerypro.fragments.list.section.video;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.ss.gallerypro.data.VideoHelper;
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

public class VideoFragment extends BaseTimelineFragment implements ITimelineView {
    private static final String TAG = "VideoFragment";

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BaseActionMode createActionMode() {
        return new VideoActionMode(this);
    }

    @Override
    protected ITimelineRepository createModel() {
        return new VideoRepository(mAttachedActivity);
    }

    @Override
    protected ITimelinePresenter createPresenter(ITimelineRepository model) {
        return new VideoPresenter(this, model);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(() -> {
                mSwipeRefreshLayout.setRefreshing(true);
                listener.onRefresh();
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
        return new VideoAdapter(this, mAttachedActivity, getSortingMode(), getSortingOrder());
    }

    @Override
    protected int getColumnRecycleView() {
        return 3;
    }

    @Override
    protected void createSwipeEvent() {
        presenter.getMedias(MediaFilter.VIDEO);
    }

    @Override
    protected SortingMode getSortModeFromPref() {
        return VideoHelper.getSortingMode();
    }

    @Override
    protected SortingOrder getSortOrderFromPref() {
        return VideoHelper.getSortingOrder();
    }

    @Override
    protected void setSortModeToPref(SortingMode sortingMode) {
        VideoHelper.setSortingMode(sortingMode);
    }

    @Override
    protected void setSortOrderToPref(SortingOrder sortingOrder) {
        VideoHelper.setSortingOrder(sortingOrder);
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
        for(int i=0; i<getListDeletedPosition().size(); i++) {
            getAdapter().removeMedia(getListDeletedPosition().get(i));
        }
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
                args.putBoolean("isImage", false);
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

    }

    @Override
    public void onDelete(ArrayList<Integer> pos, ArrayList<MediaItem> mediaItems) {
        if(pos.size() > 0) {
            setListDeletedPosition(pos);
            presenter.deleteMedias(mediaItems);
        }
    }

    @Override
    public void onChange() {
        Log.v("tunm1", "VideoFragment refresh data");
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(() -> {
                if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);
                listener.onRefresh();
                Log.v(TAG, "reload data.");
            });
        }
    }
}
