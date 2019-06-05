package com.ss.gallerypro.fragments.listHeader.timeline.adapter;

import android.content.Context;
import android.view.View;

import com.ss.gallerypro.R;
import com.ss.gallerypro.fragments.listHeader.abstraction.BaseTimelineAdapter;
import com.ss.gallerypro.fragments.listHeader.timeline.holder.ContentViewHolder;
import com.ss.gallerypro.fragments.listHeader.timeline.holder.HeaderViewHolder;

public class TimelineAdapter extends BaseTimelineAdapter<HeaderViewHolder, ContentViewHolder> {

    public TimelineAdapter(Context context) {
        super(context);
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
