package com.ss.gallerypro.fragments.list.abstraction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import com.ss.gallerypro.data.LayoutType;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;

public abstract class BaseListViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected SparseBooleanArray mSelectedItemsIds;
    protected SortingOrder mSortingOrder;
    protected SortingMode mSortingMode;
    protected LayoutType mLayoutType;
    private CheckedItemInterface mCheckedItemInterface;

    public BaseListViewAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder, LayoutType layoutType) {
        this.mContext = context;
        this.mSortingMode = sortingMode;
        this.mSortingOrder = sortingOrder;
        this.mLayoutType = layoutType;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public BaseListViewAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder) {
        this.mContext = context;
        this.mSortingMode = sortingMode;
        this.mSortingOrder = sortingOrder;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        throw new AssertionError("undefined view type : " + viewType);
    }

    public SortingOrder getSortingOrder() {
        return mSortingOrder;
    }

    public SortingMode getSortingMode() {
        return mSortingMode;
    }

    public LayoutType getLayoutType() {
        return mLayoutType;
    }

    public void changeSortingOrder(SortingOrder sortingOrder) {
        this.mSortingOrder = sortingOrder;
        reverseDataOrder();
        notifyDataSetChanged();
    }

    public void changeSortingMode(SortingMode sortingMode){
        this.mSortingMode = sortingMode;
        sort();
    }

    public void changeLayoutType(LayoutType layoutType) {
        this.mLayoutType = layoutType;
    }


    public interface CheckedItemInterface {
        void change(int numbItemCheck);
    }

    public void setItemCheckedInterface(CheckedItemInterface checkedItemInterface) {
        this.mCheckedItemInterface = checkedItemInterface;
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

    protected abstract void reverseDataOrder();

    protected abstract void sort();
}
