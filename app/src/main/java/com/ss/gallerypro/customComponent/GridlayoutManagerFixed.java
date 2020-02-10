package com.ss.gallerypro.customComponent;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class GridlayoutManagerFixed extends GridLayoutManager {
    private boolean isScrollEnabled = true;

    public GridlayoutManagerFixed(Context context, int spanCount) {
        super(context, spanCount);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            Log.e("tu.nm1", "meet a IOOBE in RecyclerView");
        }
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
