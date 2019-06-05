package com.ss.gallerypro.fragments.listHeader.abstraction;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ss.gallerypro.R;
import com.ss.gallerypro.view.SquareImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseContentViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.ivTimelineThumbnail)
    SquareImageView ivTimelineThumbnail;

    @BindView(R.id.ivTimelineCheckbox)
    ImageView ivTimelineCheckbox;

    protected BaseContentViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
