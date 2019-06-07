package com.ss.gallerypro.fragments.list.split.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ss.gallerypro.R;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.sort.PhotoComparators;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.list.normal.abstraction.BaseListViewAdapter;
import com.ss.gallerypro.utils.Convert;

import java.util.ArrayList;
import java.util.Collections;

import static android.support.constraint.Constraints.TAG;

public class VideosAdapter extends BaseListViewAdapter<VideoViewHolder> {

    private ArrayList<MediaItem> mMediaList;
    VideosAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder) {
        super(context, sortingMode, sortingOrder);
        mMediaList = new ArrayList<>();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        MediaItem mItem = mMediaList.get(position);

        RequestOptions options = new RequestOptions()
                .useAnimationPool(true)
                .override(holder.ivVideoThumb.getWidth(), holder.ivVideoThumb.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(mContext)
                .load(mItem.getPathMediaItem())
                .thumbnail(0.1f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(holder.ivVideoThumb);

        holder.ivCheckbox.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.GONE);
        holder.tvDuration.setText(Convert.convertSecondsToHMmSs(Long.parseLong(mItem.getDuration())));
        super.onBindViewHolder(holder, position);
    }

    @Override
    protected void reverseDataOrder() {
        Collections.reverse(mMediaList);
    }

    @Override
    protected void sort() {
        mMediaList.sort(PhotoComparators.getComparator(mSortingMode, mSortingOrder));
    }

    @Override
    public int getItemCount() {
        return mMediaList.size();
    }

    public void setDataList(ArrayList<MediaItem> dataList) {
        mMediaList.clear();
        mMediaList.addAll(dataList);
        notifyDataSetChanged();
    }

    public ArrayList<MediaItem> getMediaList() {
        return mMediaList;
    }

    // create to use animation
    public void removeVideo(int position) {
        if (position < 0 || position >= mMediaList.size()) {
            // Warning, invalid position
            Log.v(TAG , "FC occur!");
        } else {
            mMediaList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
