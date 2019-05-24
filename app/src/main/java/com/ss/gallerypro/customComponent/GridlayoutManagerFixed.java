package com.ss.gallerypro.customComponent;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class GridlayoutManagerFixed extends GridLayoutManager {
    public GridlayoutManagerFixed(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("tu.nm1", "meet a IOOBE in RecyclerView");
        }
    }
}
