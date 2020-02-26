package com.tunm.gallerypro.fragments.list.section.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tunm.gallerypro.R;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.fragments.list.section.abstraction.BaseTimelineAdapter;
import com.tunm.gallerypro.fragments.list.section.abstraction.ViewType;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.ContentModel;
import com.tunm.gallerypro.fragments.list.section.timeline.holder.HeaderViewHolder;
import com.tunm.gallerypro.fragments.list.section.video.holder.VideoContentViewHolder;
import com.tunm.gallerypro.utils.Convert;

public class VideoAdapter extends BaseTimelineAdapter<HeaderViewHolder, VideoContentViewHolder> {
    VideoAdapter(Fragment fragment, Context context, SortingMode sortingMode, SortingOrder sortingOrder) {
        super(fragment, context, sortingMode, sortingOrder);
    }

    @Override
    protected HeaderViewHolder createHeaderViewHolder(Context context, View view) {
        return new HeaderViewHolder(context, view);
    }

    @Override
    protected VideoContentViewHolder createContentViewHolder(Context context, View view) {
        return new VideoContentViewHolder(context, view);
    }

    @Override
    protected int getHeaderItemLayout() {
        return R.layout.item_timeline_section;
    }

    @Override
    protected int getContentItemLayout() {
        return R.layout.item_timeline_video_content;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(ViewType.CONTENT_VIEW_TYPE == getItemViewType(position)) {
            ContentModel cModel = (ContentModel) mListData.get(position);
            String duration = (cModel.mMediaItem.getDuration() == null) ? "0:00" : Convert.convertSecondsToHMmSs(Long.parseLong(cModel.mMediaItem.getDuration()));
            contentHolder.tvDuration.setText(duration);
        }
    }
}
