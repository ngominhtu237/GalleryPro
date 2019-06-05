package com.ss.gallerypro.fragments.listHeader.timeline;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.provider.CPHelper;
import com.ss.gallerypro.fragments.listHeader.abstraction.BaseHeaderAdapter;
import com.ss.gallerypro.fragments.listHeader.abstraction.BaseHeaderFragment;
import com.ss.gallerypro.fragments.listHeader.abstraction.ItemInterface;
import com.ss.gallerypro.fragments.listHeader.timeline.adapter.TimelineAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends BaseHeaderFragment {

    private TimelineAdapter adapter;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        adapter.setData(getListDataFromDB());
        return view;
    }

    @Override
    protected void setSpanCountItem() {
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (BaseHeaderAdapter.SECTION_VIEW == adapter.getItemViewType(position)) {
                    return 3;
                }
                return 1;
            }
        });
    }

    @Override
    protected int getNumberColumn() {
        return 3;
    }

    @Override
    protected void initAndSetAdapter() {
        adapter = new TimelineAdapter(getContext());
        mRecyclerView.setAdapter(adapter);
    }

    private ArrayList<ItemInterface> getListDataFromDB() {
        return CPHelper.getMediaTimeline(mAttachedActivity);
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

}
