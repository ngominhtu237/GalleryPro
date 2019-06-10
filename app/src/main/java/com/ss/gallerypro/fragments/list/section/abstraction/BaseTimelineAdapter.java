package com.ss.gallerypro.fragments.list.section.abstraction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.data.provider.CPHelper;
import com.ss.gallerypro.data.sort.PhotoComparators;
import com.ss.gallerypro.data.sort.SortingMode;
import com.ss.gallerypro.data.sort.SortingOrder;
import com.ss.gallerypro.fragments.ICheckedItem;
import com.ss.gallerypro.fragments.RecycleViewClickListener;
import com.ss.gallerypro.fragments.list.section.abstraction.model.ContentModel;
import com.ss.gallerypro.fragments.list.section.abstraction.model.HeaderModel;
import com.ss.gallerypro.fragments.list.section.abstraction.model.IItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class BaseTimelineAdapter<HEADER extends BaseHeaderViewHolder, CONTENT extends BaseContentViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected ArrayList<MediaItem> mediaItems = new ArrayList<>();
    protected ArrayList<IItem> mListData = new ArrayList<>();
    WeakReference<Context> mContextWeakReference;
    private SortingOrder mSortingOrder;
    private SortingMode mSortingMode;

    protected HEADER headerHolder;
    protected CONTENT contentHolder;

    private RecycleViewClickListener recycleViewClickListener;
    protected SparseBooleanArray mSelectedItemsIds;
    private ICheckedItem checkedItemListener;

    public BaseTimelineAdapter(Context context, SortingMode sortingMode, SortingOrder sortingOrder ) {
        this.mContextWeakReference = new WeakReference<>(context);
        this.mSortingMode = sortingMode;
        this.mSortingOrder = sortingOrder;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = mContextWeakReference.get();
        if(viewType == ViewType.HEADER_VIEW_TYPE) {
            return createHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(getHeaderItemLayout(), parent, false));
        } else {
            return createContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(getContentItemLayout(), parent, false));
        }
    }

    protected abstract HEADER createHeaderViewHolder(View view);

    protected abstract CONTENT createContentViewHolder(View view);

    protected abstract int getHeaderItemLayout();

    protected abstract int getContentItemLayout();

    @Override
    public int getItemViewType(int position) {
        if (mListData.get(position).isHeader()) {
            return ViewType.HEADER_VIEW_TYPE;
        } else {
            return ViewType.CONTENT_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Context context = mContextWeakReference.get();

        if (context == null) {
            return;
        }

        if(ViewType.HEADER_VIEW_TYPE == getItemViewType(position)) {
            headerHolder = (HEADER) holder;
            HeaderModel sModel = (HeaderModel) mListData.get(position);
            headerHolder.tvTitle.setText(sModel.title);
            return;
        }

        contentHolder = (CONTENT) holder;
        ContentModel cModel = (ContentModel) mListData.get(position);

        RequestOptions options = new RequestOptions()
                .useAnimationPool(true)
                .override(contentHolder.ivTimelineThumbnail.getWidth(), contentHolder.ivTimelineThumbnail.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(context)
                .load(cModel.mMediaItem.getPathMediaItem())
                .thumbnail(0.1f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(contentHolder.ivTimelineThumbnail);

        contentHolder.ivTimelineCheckbox.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(view -> recycleViewClickListener.onClick(view, holder.getAdapterPosition()));

        holder.itemView.setOnLongClickListener(view -> {
            recycleViewClickListener.onLongClick(view, holder.getAdapterPosition());
            return true;
        });
    }

    public SortingOrder getSortingOrder() {
        return mSortingOrder;
    }

    public SortingMode getSortingMode() {
        return mSortingMode;
    }

    @Override
    public int getItemCount() {
        if (mListData == null) return 0;
        return mListData.size();
    }

    public void setMediaItems(ArrayList<MediaItem> mediaItems) {
        this.mediaItems = mediaItems;
    }

    public ArrayList<MediaItem> getMediaItems() {
        return mediaItems;
    }

    public void changeSorting(SortingMode sortingMode, SortingOrder sortingOrder){
        this.mSortingMode = sortingMode;
        this.mSortingOrder = sortingOrder;
        mediaItems.sort(PhotoComparators.getComparator(mSortingMode, mSortingOrder));

        // create data structure for adapter
        mListData.clear();
        mListData = createDataForAdapter(mediaItems, sortingMode);
        notifyDataSetChanged();
    }

    private ArrayList<IItem> createDataForAdapter(ArrayList<MediaItem> mediaItems, SortingMode sortingMode) {
        switch (sortingMode) {
            case DATE_TAKEN:
                return CPHelper.createDataSortByDateTaken(mediaItems);
            case LAST_MODIFIED:
                return CPHelper.createDataSortByLastModified(mediaItems);
            case NAME:
                return CPHelper.createDataSortByName(mediaItems);
            case SIZE:
                return CPHelper.createDataSortBySize(mediaItems);
        }
        return null;
    }

    public void setRecycleViewClickListener(RecycleViewClickListener recycleViewClickListener) {
        this.recycleViewClickListener = recycleViewClickListener;
    }

    public void setCheckedItemListener(ICheckedItem checkedItemListener) {
        this.checkedItemListener = checkedItemListener;
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        checkedItemListener.change(mSelectedItemsIds.size());
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
