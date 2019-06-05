package com.ss.gallerypro.fragments.listHeader.timeline;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.data.provider.CPHelper;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.listHeader.abstraction.BaseTimelineAdapter;
import com.ss.gallerypro.fragments.listHeader.abstraction.BaseTimelineFragment;
import com.ss.gallerypro.fragments.listHeader.abstraction.model.IItem;
import com.ss.gallerypro.fragments.listHeader.abstraction.model.ITimelineRepository;
import com.ss.gallerypro.fragments.listHeader.abstraction.presenter.ITimelinePresenter;
import com.ss.gallerypro.fragments.listHeader.abstraction.view.ITimelineView;
import com.ss.gallerypro.fragments.listHeader.timeline.adapter.TimelineAdapter;

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
        adapter.setData(getListDataFromDB());
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
        return new TimelineAdapter(mAttachedActivity);
    }

    @Override
    protected int getColumnRecycleView() {
        return 3;
    }

    private ArrayList<IItem> getListDataFromDB() {
        return CPHelper.getMediaTimeline(mAttachedActivity, MediaFilter.IMAGE, SortingMode.SIZE, SortingOrder.ASCENDING);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_timeline;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.timeline_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.general_timeline_items, true);
        menu.setGroupVisible(R.id.edit_mode_items, false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_timeline_camera:
                return true;

            case R.id.action_timeline_about:
                return true;

            case R.id.action_timeline_setting:
                return true;

            case R.id.action_timeline_delete:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetTimelineSuccess(ArrayList<MediaItem> mediaItems) {

    }

    @Override
    public void onDeleteTimelineSuccess() {

    }
}
