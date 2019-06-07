package com.ss.gallerypro.fragments.list.section.timeline;

import android.content.Context;
import android.view.View;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.list.section.abstraction.BaseTimelineAdapter;
import com.ss.gallerypro.fragments.list.section.timeline.holder.ContentViewHolder;
import com.ss.gallerypro.fragments.list.section.timeline.holder.HeaderViewHolder;

public class TimelineAdapter extends BaseTimelineAdapter<HeaderViewHolder, ContentViewHolder> {

    public TimelineAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder) {
        super(context, sortingMode, sortingOrder);
    }

    @Override
    protected HeaderViewHolder createHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    protected ContentViewHolder createContentViewHolder(View view) {
        return new ContentViewHolder(view);
    }

    @Override
    protected int getHeaderItemLayout() {
        return R.layout.item_timeline_section;
    }

    @Override
    protected int getContentItemLayout() {
        return R.layout.item_timeline_content;
    }
}
