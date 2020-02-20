package com.tubeeapp.gallerypro.fragments.list.split.video;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tubeeapp.gallerypro.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class VideoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ivVideoThumb)
    ImageView ivVideoThumb;

    @BindView(R.id.ivCheckbox)
    ImageView ivCheckbox;

    @BindView(R.id.ivPlayIcon)
    ImageView ivPlayIcon;

    @BindView(R.id.tvDuration)
    TextView tvDuration;

    VideoViewHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
    }
}
