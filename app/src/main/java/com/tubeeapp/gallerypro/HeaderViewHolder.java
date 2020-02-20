package com.tubeeapp.gallerypro;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderViewHolder {

    @BindView(R.id.nav_header_bg)
    protected ImageView mNavHeaderBg;

    @BindView(R.id.nav_header_title)
    protected TextView mTitle;

    @BindView(R.id.tvPhotoCount)
    protected TextView tvPhotoCount;

    @BindView(R.id.tvPhotoSize)
    protected TextView tvPhotoSize;

    @BindView(R.id.tvVideoCount)
    protected TextView tvVideoCount;

    @BindView(R.id.tvVideoSize)
    protected TextView tvVideoSize;

    @BindView(R.id.tvCountAlbum)
    protected TextView tvAlbumCount;

    @BindView(R.id.tvAlbumSize)
    protected TextView tvAlbumSize;

    HeaderViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
