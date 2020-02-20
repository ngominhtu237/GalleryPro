package com.tubeeapp.gallerypro.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.TextView;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.data.Bucket;
import com.tubeeapp.gallerypro.data.MediaItem;
import com.tubeeapp.gallerypro.theme.ui.BaseDialogTheme;

import java.util.ArrayList;
import java.util.Objects;

import static com.tubeeapp.gallerypro.data.AlbumHelper.getSizeAlbum;
import static com.tubeeapp.gallerypro.data.utils.DataUtils.readableFileSize;

public class AlbumPictureDetailsDialog extends BaseDialogTheme {
    private Bucket mReceiveBucket;
    private ArrayList<MediaItem> mMediaList;
    public AlbumPictureDetailsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tvAlbumName = findViewById(R.id.textView_album_picture_name);
        TextView tvAlbumPath = findViewById(R.id.textView_album_picture_path);
        TextView tvAlbumSize = findViewById(R.id.textView_album_picture_size);
        TextView tvAlbumCount = findViewById(R.id.textView_album_picture_count);
        Button btOK = findViewById(R.id.button_album_picture_ok);
        Objects.requireNonNull(btOK).setOnClickListener(v -> dismiss());
        if(mReceiveBucket != null) {
            Objects.requireNonNull(tvAlbumName).setText(mReceiveBucket.getName());
            Objects.requireNonNull(tvAlbumPath).setText(mReceiveBucket.getPathToAlbum());
            Objects.requireNonNull(tvAlbumSize).setText(readableFileSize(getSizeAlbum(mMediaList)));
            Objects.requireNonNull(tvAlbumCount).setText(String.valueOf(mMediaList.size()));
        }
        refreshTheme();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_album_details;
    }

    @Override
    protected float getWidth() {
        return 0.85f;
    }

    public void setCurrentAlbum(Bucket receivedBucket) {
        mReceiveBucket = receivedBucket;
    }

    public void setMediaList(ArrayList<MediaItem> list) {
        mMediaList = list;
    }
}
