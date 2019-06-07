package com.ss.gallerypro.fragments.listHeader.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ss.gallerypro.R;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.listHeader.abstraction.BaseTimelineAdapter;
import com.ss.gallerypro.fragments.listHeader.abstraction.ViewType;
import com.ss.gallerypro.fragments.listHeader.abstraction.model.ContentModel;
import com.ss.gallerypro.fragments.listHeader.timeline.holder.HeaderViewHolder;
import com.ss.gallerypro.fragments.listHeader.video.holder.VideoContentViewHolder;
import com.ss.gallerypro.utils.Convert;

public class VideoAdapter extends BaseTimelineAdapter<HeaderViewHolder, VideoContentViewHolder> {
    VideoAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder) {
        super(context, sortingMode, sortingOrder);
    }

    @Override
    protected HeaderViewHolder createHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    protected VideoContentViewHolder createContentViewHolder(View view) {
        return new VideoContentViewHolder(view);
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
            contentHolder.tvDuration.setText(Convert.convertSecondsToHMmSs(Long.parseLong(cModel.mMediaItem.getDuration())));
        }
    }
}
