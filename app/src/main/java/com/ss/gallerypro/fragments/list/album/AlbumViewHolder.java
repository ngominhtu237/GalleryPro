package com.ss.gallerypro.fragments.list.album;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ss.gallerypro.R;
import com.ss.gallerypro.view.SquareImageView;

public class AlbumViewHolder extends RecyclerView.ViewHolder {

    SquareImageView ivThumbnail;
    TextView tvAlbumName;
    TextView tvCount;
    ImageView ivCheckbox;
    TextView tvAlbumPath;

    AlbumViewHolder(View itemView) {
        super(itemView);
        this.ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
        this.tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
        this.tvCount = itemView.findViewById(R.id.tvCount);
        this.ivCheckbox = itemView.findViewById(R.id.ivCheckbox);
        this.tvAlbumPath = itemView.findViewById(R.id.tvDirectory);
    }
}
