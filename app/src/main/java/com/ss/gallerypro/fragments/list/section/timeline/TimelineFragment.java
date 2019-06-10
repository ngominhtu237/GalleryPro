package com.ss.gallerypro.fragments.list.section.timeline;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends BaseTimelineFragment implements ITimelineView {

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(() -> {
                if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);
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
        return new TimelineAdapter(mAttachedActivity, getSortingMode(), getSortingOrder());
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
//            Intent intent = new Intent(getContext(), PicturePreview.class);
//            intent.putExtra("current_image_position", position);
//            intent.putExtra("album_path", mReceiveBucket.getPathToAlbum());
//            intent.putExtra("list_image", adapter.getMediaList());
//            startActivity(intent);
        }
    }

    @Override
    public void onLongClick(View view, int position) {
        setEnableSwipeRefresh(false);
        onListItemSelect(position);
    }
}
