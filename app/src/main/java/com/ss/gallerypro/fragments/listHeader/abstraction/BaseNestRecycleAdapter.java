package com.ss.gallerypro.fragments.listHeader.abstraction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.fragments.RecycleViewClickListener;

import java.util.ArrayList;

public abstract class BaseNestRecycleAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>  {
    protected Context mContext;
    protected ArrayList<MediaItem> mListMediaItem = new ArrayList<>();

    private SparseBooleanArray mSelectedItemsIds;
    private CheckedItemInterface mCheckedItemInterface;
    private RecycleViewClickListener recycleViewClickListener;

    public BaseNestRecycleAdapter(Context mContext, ArrayList<MediaItem> listMediaItem) {
        this.mContext = mContext;
        this.mListMediaItem.clear();
        this.mListMediaItem.addAll(listMediaItem);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getNestItemLayout(), parent, false);
        return createNestViewHolder(view);
    }

    protected abstract VH createNestViewHolder(View view);

    protected abstract int getNestItemLayout();

    @Override
    public int getItemCount() {
        return mListMediaItem.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
//        holder.itemView.setOnClickListener(view -> recycleViewClickListener.onClick(view, holder.getAdapterPosition()));
//
//        holder.itemView.setOnLongClickListener(view -> {
//            recycleViewClickListener.onLongClick(view, holder.getAdapterPosition());
//            return true;
//        });
    }

    public interface CheckedItemInterface {
        void change(int numbItemCheck);
    }

    public void setItemCheckedInterface(CheckedItemInterface checkedItemInterface) {
        this.mCheckedItemInterface = checkedItemInterface;
    }

    public void setRecycleViewClickListener(RecycleViewClickListener recycleViewClickListener) {
        this.recycleViewClickListener = recycleViewClickListener;
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
    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        mCheckedItemInterface.change(mSelectedItemsIds.size());
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
