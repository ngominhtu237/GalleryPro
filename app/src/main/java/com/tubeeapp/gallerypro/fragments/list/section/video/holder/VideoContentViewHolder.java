package com.tubeeapp.gallerypro.fragments.list.section.video.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.fragments.list.section.abstraction.BaseContentViewHolder;

import butterknife.BindView;

public class VideoContentViewHolder extends BaseContentViewHolder {

    @BindView(R.id.ivPlayIcon)
    ImageView ivPlayIcon;

    @BindView(R.id.tvDuration)
    public
    TextView tvDuration;

    public VideoContentViewHolder(Context context, View view) {
        super(context, view);
    }
}
