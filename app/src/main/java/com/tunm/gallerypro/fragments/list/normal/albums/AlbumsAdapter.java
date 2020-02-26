package com.tunm.gallerypro.fragments.list.normal.albums;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.data.Bucket;
import com.tunm.gallerypro.data.LayoutType;
import com.tunm.gallerypro.data.sort.AlbumsComparators;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.fragments.list.normal.abstraction.BaseListViewAdapter;
import com.tunm.gallerypro.utils.view.IconUtils;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumsAdapter extends BaseListViewAdapter<AlbumViewHolder> {

    private static final String TAG = "AlbumsAdapter";

    private ArrayList<Bucket> mBuckets; // chưa abstract

    AlbumsAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder, LayoutType layoutType) {
        super(context, sortingMode, sortingOrder, layoutType);
        mBuckets = new ArrayList<>();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate((mLayoutType == LayoutType.GRID) ? R.layout.item_album_grid : R.layout.item_album_list, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, final int position) {
        RequestOptions options = new RequestOptions()
                .useAnimationPool(true)
                .override(holder.ivThumbnail.getWidth(), holder.ivThumbnail.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(mContext)
                .load(mBuckets.get(position).getPathPhotoCover())
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(holder.ivThumbnail);

        holder.tvAlbumName.setText(mBuckets.get(position).getName());
        String c = String.valueOf(mBuckets.get(position).getCount());
        if(mLayoutType == LayoutType.LIST) {
            c = "(" + c + ")";
        } else {
            if(Integer.parseInt(c) > 1) {
                c = c + " items";
            } else {
                c = c + " item";
            }
        }
        holder.tvCount.setText(c);
        holder.ivCheckbox.setImageDrawable(IconUtils.createSelectedIcon(mContext, mContext.getColor(R.color.colorDarkAccent)));
        holder.ivCheckbox.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.GONE);
        if(mLayoutType == LayoutType.LIST) {
            holder.tvAlbumPath.setVisibility(View.VISIBLE);
            holder.tvAlbumPath.setText(mBuckets.get(position).getPathToAlbum());
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mBuckets.size();
    }

    @Override
    protected void reverseDataOrder() {
        Collections.reverse(mBuckets);
    }

    @Override
    protected void sort() {
        mBuckets.sort(AlbumsComparators.getComparator(mSortingMode, mSortingOrder));
    }

    public void setDataList(ArrayList<Bucket> buckets) {
        mBuckets.clear();
        mBuckets.addAll(buckets);
        notifyDataSetChanged();
    }

    // create to use animation
    public void removeAlbum(int position) {
        mBuckets.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<Bucket> getBuckets() {
        return mBuckets;
    }
}
