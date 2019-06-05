package com.ss.gallerypro.fragments.listHeader.timeline.adapter;

import android.content.Context;
import android.view.View;

import com.ss.gallerypro.R;
import com.ss.gallerypro.fragments.listHeader.abstraction.BaseHeaderAdapter;
import com.ss.gallerypro.fragments.listHeader.timeline.holder.ContentViewHolder;
import com.ss.gallerypro.fragments.listHeader.timeline.holder.SectionViewHolder;

public class TimelineAdapter extends BaseHeaderAdapter<SectionViewHolder, ContentViewHolder> {

    public TimelineAdapter(Context context) {
        super(context);
    }

    @Override
    protected SectionViewHolder createSectionViewHolder(View view) {
        return new SectionViewHolder(view);
    }

    @Override
    protected ContentViewHolder createContentViewHolder(View view) {
        return new ContentViewHolder(view);
    }

    @Override
    protected int getSectionItemLayout() {
        return R.layout.item_timeline_section;
    }

    @Override
    protected int getContentItemLayout() {
        return R.layout.item_timeline_content;
    }
}
