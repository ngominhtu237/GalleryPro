package com.ss.gallerypro.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ss.gallerypro.R;
import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{

    private static final String TAG = "VideoAdapter";
    private ArrayList<MediaItem> mVideoList;
    private Context mContext;
    private SparseBooleanArray mSelectedItemsIds;

    public VideoAdapter(Context mContext, ArrayList<MediaItem> mVideoList) {
        this.mVideoList = mVideoList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_split_item, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        MediaItem mItem = mVideoList.get(position);

        RequestOptions options = new RequestOptions()
                .useAnimationPool(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(mContext)
                .load(mItem.getPathMediaItem())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(holder.ivVideo);

        holder.ivTick.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.GONE);
        holder.ivPlay.setVisibility(mItem.getMediaType().contains("video") ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView ivVideo;
        ImageView ivTick;
        ImageView ivPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            this.ivVideo = itemView.findViewById(R.id.ivAlbumPictureThumb);
            this.ivTick = itemView.findViewById(R.id.ivCheckbox);
            this.ivPlay = itemView.findViewById(R.id.ivPlayIcon);
        }
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
