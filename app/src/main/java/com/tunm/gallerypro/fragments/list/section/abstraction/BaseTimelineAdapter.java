package com.tunm.gallerypro.fragments.list.section.abstraction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.data.provider.CPHelper;
import com.tunm.gallerypro.data.sort.PhotoComparators;
import com.tunm.gallerypro.data.sort.SortingMode;
import com.tunm.gallerypro.data.sort.SortingOrder;
import com.tunm.gallerypro.fragments.ICheckedItem;
import com.tunm.gallerypro.fragments.RecycleViewClickListener;
import com.tunm.gallerypro.fragments.ViewHolderListener;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.ContentModel;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.HeaderModel;
import com.tunm.gallerypro.fragments.list.section.abstraction.model.IItem;
import com.tunm.gallerypro.theme.ColorTheme;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class BaseTimelineAdapter<HEADER extends BaseHeaderViewHolder, CONTENT extends BaseContentViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BaseTimelineAdapter";
    private ArrayList<MediaItem> mediaItems = new ArrayList<>();
    protected ArrayList<IItem> mListData = new ArrayList<>();
    WeakReference<Context> mContextWeakReference;
    private SortingOrder mSortingOrder;
    private SortingMode mSortingMode;

    protected HEADER headerHolder;
    protected CONTENT contentHolder;

    private RecycleViewClickListener recycleViewClickListener;
    private SparseBooleanArray mSelectedItemsIds;
    private ICheckedItem checkedItemListener;

    private ViewHolderListener viewHolderListener;
    private ColorTheme mColorTheme;
    private Context mContext;

    public BaseTimelineAdapter(Fragment fragment, Context context, SortingMode sortingMode, SortingOrder sortingOrder ) {
        this.mContextWeakReference = new WeakReference<>(context);
        this.mSortingMode = sortingMode;
        this.mSortingOrder = sortingOrder;
        mSelectedItemsIds = new SparseBooleanArray();
        mColorTheme = new ColorTheme(context);
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = mContextWeakReference.get();
        if(viewType == ViewType.HEADER_VIEW_TYPE) {
            return createHeaderViewHolder(context, LayoutInflater.from(parent.getContext()).inflate(getHeaderItemLayout(), parent, false));
        } else {
            return createContentViewHolder(context, LayoutInflater.from(parent.getContext()).inflate(getContentItemLayout(), parent, false));
        }
    }

    protected abstract HEADER createHeaderViewHolder(Context context, View view);

    protected abstract CONTENT createContentViewHolder(Context context, View view);

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
            if(mColorTheme.isDarkTheme()) {
                headerHolder.tvTitle.setTextColor(mContext.getColor(R.color.colorDarkAccent));
            } else {
                headerHolder.tvTitle.setTextColor(mColorTheme.getAccentColor());
            }
            return;
        }

        contentHolder = (CONTENT) holder;
        ContentModel cModel = (ContentModel) mListData.get(position);

        RequestOptions options = new RequestOptions()
                .useAnimationPool(true)
                .override(contentHolder.ivTimelineThumbnail.getWidth(), contentHolder.ivTimelineThumbnail.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(cModel.mMediaItem.getPathMediaItem())
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        viewHolderListener.onLoadCompleted();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                            target, DataSource dataSource, boolean isFirstResource) {
                        viewHolderListener.onLoadCompleted();
                        return false;
                    }
                })
                .into(contentHolder.ivTimelineThumbnail);

        int positionInSection = getPositionInSection(holder.getAdapterPosition());

        // set transition name
//        contentHolder.ivTimelineThumbnail.setTransitionName(cModel.mMediaItem.getName());
//        Log.v("transition send name ", cModel.mMediaItem.getName());

        contentHolder.ivTimelineCheckbox.setVisibility(mSelectedItemsIds.get(positionInSection) ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener((View view) -> recycleViewClickListener.onClick(view, positionInSection));

        holder.itemView.setOnLongClickListener(view -> {
            recycleViewClickListener.onLongClick(view, positionInSection);
            return true;
        });
    }

    /**
     * Return the item position relative to the section.
     *
     * @param position position of the item in the adapter
     * @return position of the item in the section
     */
    private int getPositionInSection(int position) {
        int currentPos = 0;
        for(int i=0; i<position; i++) {
            if(mListData.get(i) instanceof ContentModel) currentPos++;
        }

        return currentPos;
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

    public ArrayList<IItem> getListData() {
        return mListData;
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

    public void setViewHolderListener(ViewHolderListener viewHolderListener) {
        this.viewHolderListener = viewHolderListener;
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

    // create to use animation
    public void removeMedia(int position) {
        int realPos = getRealPosition(position);
        Log.v("deleted current position ", String.valueOf(position));
        Log.v("deleted real position ", String.valueOf(realPos));

        mediaItems.remove(position);
        mListData.remove(realPos);

        if((mListData.get(realPos - 1) instanceof HeaderModel) && ((realPos == mListData.size() || mListData.get(realPos) instanceof HeaderModel))) {
            mListData.remove(realPos - 1);
            notifyItemRemoved(realPos - 1);
            Log.v("deleted notifyItemRemoved header ", String.valueOf(realPos - 1));
        }
        notifyItemRemoved(realPos);
        notifyItemRangeChanged(realPos, getItemCount() - realPos);
        Log.v("deleted notifyItemRemoved item ", String.valueOf(realPos));
    }

    private int getRealPosition(int position) {
        for(int i=0; i<mListData.size(); i++) {
            if(mListData.get(i) instanceof ContentModel && ((ContentModel)mListData.get(i)).mMediaItem.getPathMediaItem().equals(mediaItems.get(position).getPathMediaItem())) return i;
        }
        return -1;
    }
}
