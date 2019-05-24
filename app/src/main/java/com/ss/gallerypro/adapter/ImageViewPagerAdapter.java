package com.ss.gallerypro.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.ss.gallerypro.R;
import com.ss.gallerypro.data.MediaItem;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class ImageViewPagerAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private ArrayList<MediaItem> mImageList;
    int selectedImagePosition;

    public ImageViewPagerAdapter(Activity activity, ArrayList<MediaItem> mImageList, int selectedImagePosition) {
        this.activity = activity;
        this.mImageList = mImageList;
        this.selectedImagePosition = selectedImagePosition;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.picture_view, container, false);

        SubsamplingScaleImageView scaleImageView = view.findViewById(R.id.ivPictureNormal); // For normal image
        PhotoView photoViewGif = view.findViewById(R.id.ivPictureGif);

        String mineType = mImageList.get(position).getMediaType();
        if(mineType.equals("image/gif")) {
            photoViewGif.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(new File(mImageList.get(position).getPathMediaItem())) // Uri of the picture
                    .into(photoViewGif);
        } else {
            scaleImageView.setVisibility(View.VISIBLE);
            scaleImageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
            scaleImageView.setImage(ImageSource.uri(mImageList.get(position).getPathMediaItem()));
        }



        ImageView ivPlay = view.findViewById(R.id.ivPlayDetail);
        ivPlay.setVisibility(mImageList.get(position).getMediaType().contains("video") ? View.VISIBLE : View.GONE);
        String mediaItemPath = mImageList.get(position).getPathMediaItem();
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mediaItemPath));
                intent.setDataAndType(Uri.parse(mediaItemPath), "video/*");
                activity.startActivity(intent);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
