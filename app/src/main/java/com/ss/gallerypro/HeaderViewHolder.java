package com.ss.gallerypro;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderViewHolder {

    @BindView(R.id.nav_header_title)
    protected TextView mTitle;

    @BindView(R.id.tvPhotoCount)
    protected TextView tvPhotoCount;

    @BindView(R.id.tvVideoCount)
    protected TextView tvVideoCount;

    @BindView(R.id.tvCountAlbum)
    protected TextView tvAlbumCount;

    HeaderViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
