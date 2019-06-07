package com.ss.gallerypro.fragments.list.section.video.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ss.gallerypro.R;
import com.ss.gallerypro.fragments.list.section.abstraction.BaseContentViewHolder;

import butterknife.BindView;

public class VideoContentViewHolder extends BaseContentViewHolder {

    @BindView(R.id.ivPlayIcon)
    ImageView ivPlayIcon;

    @BindView(R.id.tvDuration)
    public
    TextView tvDuration;

    public VideoContentViewHolder(View view) {
        super(view);
    }
}
