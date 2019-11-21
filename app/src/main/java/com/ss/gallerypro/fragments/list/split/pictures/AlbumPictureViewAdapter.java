package com.ss.gallerypro.fragments.list.split.pictures;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

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

import java.util.ArrayList;
import java.util.Collections;

import static android.support.constraint.Constraints.TAG;

public class AlbumPictureViewAdapter extends BaseListViewAdapter<AlbumPicturesViewHolder> {

    private ArrayList<MediaItem> mMediaList;
    AlbumPictureViewAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder) {
        super(context, sortingMode, sortingOrder);
        mMediaList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AlbumPicturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_albums_item, parent, false);
        return new AlbumPicturesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumPicturesViewHolder holder, int position) {
        MediaItem mItem = mMediaList.get(position);

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
        return mMediaList.size();
    }

    @Override
    protected void reverseDataOrder() {
        Collections.reverse(mMediaList);
    }

    @Override
    protected void sort() {
        mMediaList.sort(PhotoComparators.getComparator(mSortingMode, mSortingOrder));
    }

    public void setDataList(ArrayList<MediaItem> medias) {
        mMediaList.clear();
        mMediaList.addAll(medias);
        notifyDataSetChanged();
    }

    public ArrayList<MediaItem> getMediaList() {
        return mMediaList;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AlbumPicturesViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    // create to use animation
    public void removeImage(int position) {
        if (position < 0 || position >= mMediaList.size()) {
            // Warning, invalid position
            Log.v(TAG , "FC occur!");
        } else {
            mMediaList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
