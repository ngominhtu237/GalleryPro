package com.ss.gallerypro.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ss.gallerypro.data.sort.PhotoComparators;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;

import java.util.ArrayList;
import java.util.Collections;

public class PhotoSplitViewAdapter extends RecyclerView.Adapter<PhotoSplitViewAdapter.ViewHolder> {

    private static final String TAG = "PhotoSplitViewAdapter";

    private ArrayList<MediaItem> mImageList;
    private Context mContext;
    private SparseBooleanArray mSelectedItemsIds;

    // default
    private SortingOrder sortingOrder;
    private SortingMode sortingMode;

    public PhotoSplitViewAdapter(Context mContext, SortingMode sortingMode, SortingOrder sortingOrder, ArrayList<MediaItem> mImageList) {
        this.mImageList = mImageList;
        this.mContext = mContext;
        mSelectedItemsIds = new SparseBooleanArray();
        this.sortingMode = sortingMode;
        this.sortingOrder = sortingOrder;
    }

    public SortingOrder sortingOrder() {
        return sortingOrder;
    }

    public SortingMode sortingMode() {
        return sortingMode;
    }

    public void changeSortingMode(SortingMode sortingMode){
        this.sortingMode = sortingMode;
        sort();
    }

    public void sort() {
        Collections.sort(mImageList, PhotoComparators.getComparator(sortingMode, sortingOrder));
        notifyDataSetChanged();
    }

    public void changeSortingOrder(SortingOrder sortingOrder) {
        this.sortingOrder = sortingOrder;
        reverseOrder();
        notifyDataSetChanged();
    }

    private void reverseOrder() {
        Collections.reverse(mImageList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_split_item, parent, false);
        return new PhotoSplitViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MediaItem mItem = mImageList.get(position);

        RequestOptions options = new RequestOptions()
                .useAnimationPool(true)
                .override(holder.ivAlbumPictureThumb.getWidth(), holder.ivAlbumPictureThumb.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(mContext)
                .load(mItem.getPathMediaItem())
                .thumbnail(0.1f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(holder.ivAlbumPictureThumb);

        holder.ivCheckbox.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.GONE);
        holder.ivPlayIcon.setVisibility(mItem.getMediaType().contains("video") ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView ivAlbumPictureThumb;
        ImageView ivCheckbox;
        ImageView ivPlayIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            this.ivAlbumPictureThumb = itemView.findViewById(R.id.ivAlbumPictureThumb);
            this.ivCheckbox = itemView.findViewById(R.id.ivCheckbox);
            this.ivPlayIcon = itemView.findViewById(R.id.ivPlayIcon);
        }
    }

    public void updateData(ArrayList<MediaItem> newBuckets) {
        this.mImageList = newBuckets;
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

    // create to use animation
    public void removeImage(int position) {
        if (position < 0 || position >= mImageList.size()) {
            // Warning, invalid position
            Log.v(TAG , "FC occur!");
        } else {
            mImageList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
