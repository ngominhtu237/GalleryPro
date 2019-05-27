package com.ss.gallerypro.fragments.list.albums.pictures;

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
import com.ss.gallerypro.fragments.list.abstraction.BaseListViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

import static android.support.constraint.Constraints.TAG;

public class AlbumPictureViewAdapter extends BaseListViewAdapter<AlbumPicturesViewHolder> {

    private ArrayList<MediaItem> mImageList;
    private OnNotifyDataChanged callback;
    AlbumPictureViewAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder,ArrayList<MediaItem> mImageList) {
        super(context, sortingMode, sortingOrder);
        this.mImageList = mImageList;
    }

    @NonNull
    @Override
    public AlbumPicturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_split_item, parent, false);
        return new AlbumPicturesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumPicturesViewHolder holder, int position) {
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
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    @Override
    public void changeSortingOrder(SortingOrder sortingOrder) {
        super.changeSortingOrder(sortingOrder);
        callback.updateDataToView(mImageList);
    }

    @Override
    public void changeSortingMode(SortingMode sortingMode) {
        super.changeSortingMode(sortingMode);
        callback.updateDataToView(mImageList);
    }

    @Override
    protected void reverseDataOrder() {
        Collections.reverse(mImageList);
    }

    @Override
    protected void sort() {
        mImageList.sort(PhotoComparators.getComparator(mSortingMode, mSortingOrder));
    }

    public void updateData(ArrayList<MediaItem> mImageList) {
        this.mImageList = mImageList;
        notifyDataSetChanged();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AlbumPicturesViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
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

    public void setDataAdapterChangeCallback(OnNotifyDataChanged dataAdapterChangeCallback) {
        this.callback = dataAdapterChangeCallback;
    }
}
