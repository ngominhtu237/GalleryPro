package com.ss.gallerypro.fragments.list.abstraction;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.ss.gallerypro.R;
import com.ss.gallerypro.customComponent.GridlayoutManagerFixed;
import com.ss.gallerypro.data.LayoutType;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.event.RecyclerClick_Listener;
import com.ss.gallerypro.event.RecyclerTouchListener;
import com.ss.gallerypro.utils.Measure;
import com.ss.gallerypro.view.GridSpacingItemDecoration;

abstract public class BaseListFragment extends Fragment {

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected SwipeRefreshLayout.OnRefreshListener mSwipeRefreshListener;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected GridSpacingItemDecoration mGridSpacingItemDecoration;
    protected DesertPlaceholder desertPlaceholder;
    protected RecyclerView recyclerView;
    protected LayoutType mLayoutType = getLayoutType();
    protected int NUM_COLUMNS;

    protected ActionMode mActionMode;

    public BaseListFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(), container, false);
        initRecycleView(v);
        implementRecyclerViewClickListeners();
        return v;
    }

    @Override
    public void onResume() {
        if(mLayoutType == LayoutType.GRID) {
            setUpColumns();
        }
        super.onResume();
    }

    protected void implementRecyclerViewClickListeners() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, final int position) {

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
        }));
    }

    public void setUpColumns() {
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

    protected abstract void handleClickItem(int position);

    protected abstract void onListItemSelect(int position);

    protected void initRecycleView(View v) {
        recyclerView = v.findViewById(R.id.albumRecycleView);
    }

    abstract protected int getLayoutId();

    abstract protected LayoutType getLayoutType();

    abstract protected SortingMode getSortingMode();

    abstract protected SortingOrder getSortingOrder();

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
}
