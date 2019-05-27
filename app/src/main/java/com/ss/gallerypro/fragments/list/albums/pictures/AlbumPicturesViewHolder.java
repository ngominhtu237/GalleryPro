package com.ss.gallerypro.fragments.list.albums.pictures;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ss.gallerypro.R;

public class AlbumPicturesViewHolder extends RecyclerView.ViewHolder {

    ImageView ivAlbumPictureThumb;
    ImageView ivCheckbox;
    ImageView ivPlayIcon;

    AlbumPicturesViewHolder(View itemView) {
        super(itemView);
        this.ivAlbumPictureThumb = itemView.findViewById(R.id.ivAlbumPictureThumb);
        this.ivCheckbox = itemView.findViewById(R.id.ivCheckbox);
        this.ivPlayIcon = itemView.findViewById(R.id.ivPlayIcon);
    }
}
