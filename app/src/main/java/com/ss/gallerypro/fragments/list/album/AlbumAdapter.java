package com.ss.gallerypro.fragments.list.album;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ss.gallerypro.R;
import com.ss.gallerypro.data.Bucket;
import com.ss.gallerypro.data.LayoutType;
import com.ss.gallerypro.data.sort.AlbumsComparators;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.list.abstraction.BaseListViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumAdapter extends BaseListViewAdapter<AlbumViewHolder> {

    private static final String TAG = "AlbumAdapter";

    private ArrayList<Bucket> mBuckets; // chưa abstract

    AlbumAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder,LayoutType layoutType, ArrayList<Bucket> mBuckets) {
        super(context, sortingMode, sortingOrder, layoutType);
        this.mBuckets = mBuckets;
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
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(mContext)
                .load(mBuckets.get(position).getPathPhotoCover())
                .thumbnail(0.1f)
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
        /** Change background color of the selected items in list view  **/
        holder.ivCheckbox.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.GONE);
        if(mLayoutType == LayoutType.LIST) {
            holder.tvAlbumPath.setVisibility((mLayoutType == LayoutType.LIST) ? View.VISIBLE : View.GONE);
            holder.tvAlbumPath.setText(mBuckets.get(position).getPathToAlbum());
        }
    }

    @Override
    public int getItemCount() {
        return mBuckets.size();
    }

    @Override
    public void changeSortingOrder(SortingOrder sortingOrder) {
        super.changeSortingOrder(sortingOrder);
        dataAdapterChangeCallback.updateData(mBuckets);
    }

    @Override
    public void changeSortingMode(SortingMode sortingMode) {
        super.changeSortingMode(sortingMode);
        dataAdapterChangeCallback.updateData(mBuckets);
    }

    @Override
    protected void reverseDataOrder() {
        Collections.reverse(mBuckets);
        notifyDataSetChanged();
    }

    @Override
    protected void sort() {
        mBuckets.sort(AlbumsComparators.getComparator(mSortingMode, mSortingOrder));
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<Bucket> newBuckets) {
        this.mBuckets = newBuckets;
        notifyDataSetChanged();
    }

    // create to use animation
    public void removeAlbum(int pos) {
        mBuckets.remove(pos);
        notifyItemRemoved(pos);
    }
}
