package com.tunm.gallerypro.fragments.list.section.video;

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

import com.tunm.gallerypro.R;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.VideoHelper;
import com.tunm.gallerypro.data.filter.MediaFilter;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.fragments.list.section.abstraction.BaseTimelineAdapter;
import com.tunm.gallerypro.fragments.list.section.abstraction.BaseTimelineFragment;
import com.tunm.gallerypro.fragments.list.section.abstraction.actionmode.BaseActionMode;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.ITimelineRepository;
import com.tunm.gallerypro.fragments.list.section.abstraction.presenter.ITimelinePresenter;
import com.tunm.gallerypro.fragments.list.section.abstraction.view.ITimelineView;
import com.tunm.gallerypro.fragments.viewer.ImagePagerFragment;
import com.tunm.gallerypro.view.SquareImageView;

import org.jetbrains.annotations.NotNull;

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
    public View onCreateView(@NotNull @NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected BaseTimelineAdapter createAdapter() {
        return new VideoAdapter(this, mAttachedActivity, getSortingMode(), getSortingOrder());
    }

    @Override
    protected void loadData() {
        Log.v("loadData", TAG);
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
        if(mediaItems.size() > 0) {
            getAdapter().setMediaItems(mediaItems);
            getAdapter().changeSorting(getSortingMode(), getSortingOrder());
            if(mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
            if(mRecyclerView != null) mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            if(desertPlaceholder != null) desertPlaceholder.setVisibility(View.VISIBLE);
            if(mRecyclerView != null) mRecyclerView.setVisibility(View.GONE);
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
        Log.v("loadData", "update " + TAG);
        presenter.getMedias(MediaFilter.VIDEO);
    }
}
