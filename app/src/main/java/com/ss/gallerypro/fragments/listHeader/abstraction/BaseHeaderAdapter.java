package com.ss.gallerypro.fragments.listHeader.abstraction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class BaseHeaderAdapter<SECTION extends BaseSectionViewHolder, CONTENT extends BaseContentViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemInterface> mListData = new ArrayList<>();
    WeakReference<Context> mContextWeakReference;

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    public BaseHeaderAdapter(Context context) {
        this.mContextWeakReference = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = mContextWeakReference.get();
        if(viewType == SECTION_VIEW) {
            return createSectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(getSectionItemLayout(), parent, false));
        } else {
            return createContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(getContentItemLayout(), parent, false));
        }
    }

    protected abstract SECTION createSectionViewHolder(View view);

    protected abstract CONTENT createContentViewHolder(View view);

    protected abstract int getSectionItemLayout();

    protected abstract int getContentItemLayout();

    @Override
    public int getItemViewType(int position) {
        if (mListData.get(position).isSection()) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Context context = mContextWeakReference.get();

        if (context == null) {
            return;
        }

        if(SECTION_VIEW == getItemViewType(position)) {
            SECTION sectionHolder = (SECTION) holder;
            SectionModel sModel = (SectionModel) mListData.get(position);
            sectionHolder.tvTitle.setText(sModel.title);
            return;
        }

        CONTENT contentHolder = (CONTENT) holder;
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
    }



    @Override
    public int getItemCount() {
        if (mListData == null) return 0;
        return mListData.size();
    }

    public void setData(ArrayList<ItemInterface> dataList) {
        mListData.clear();
        mListData.addAll(dataList);
        notifyDataSetChanged();
    }

    public ArrayList<ItemInterface> getData() {
        return mListData;
    }
}
