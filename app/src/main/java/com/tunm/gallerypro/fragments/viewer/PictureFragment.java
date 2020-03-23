package com.tunm.gallerypro.fragments.viewer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.tunm.gallerypro.DrawerLocker;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.customComponent.ViewPagerFixed;
import com.tunm.gallerypro.data.MediaItem;
import com.tunm.gallerypro.theme.ui.SpinKitViewTheme;

import java.util.Objects;

public class PictureFragment extends Fragment implements SubsamplingScaleImageView.OnImageEventListener, View.OnClickListener, SubsamplingScaleImageView.OnStateChangedListener {

    private static final String KEY_IMAGE_OBJ = "com.ss.key.imageObj";
    private SpinKitViewTheme spinKitView;
    private ViewPagerFixed viewPager;

    private SubsamplingScaleImageView scaleImageView;

    public static PictureFragment newInstance(MediaItem mediaItem) {
        PictureFragment fragment = new PictureFragment();
        Bundle argument = new Bundle();
        argument.putSerializable(KEY_IMAGE_OBJ, mediaItem);
        fragment.setArguments(argument);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((DrawerLocker) Objects.requireNonNull(getActivity())).setDrawerEnabled(false);
        View v =  inflater.inflate(R.layout.picture_fragment, container, false);
        PhotoView photoView = v.findViewById(R.id.ivPhotoView);
        scaleImageView = v.findViewById(R.id.ivSubSamplingView);
        ImageView ivPlay = v.findViewById(R.id.ivPlayIcon);
        spinKitView = v.findViewById(R.id.spinKit);

        viewPager = (ViewPagerFixed) container;

        scaleImageView.setOnImageEventListener(this);
        scaleImageView.setOnStateChangedListener(this);
        scaleImageView.setOnClickListener(this);

        MediaItem item = null;
        if (getArguments() != null) {
            item = (MediaItem) getArguments().getSerializable(KEY_IMAGE_OBJ);
        }

        assert item != null;
        v.findViewById(R.id.ivPhotoView).setTransitionName(item.getName());

        PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
        mAttacher.setOnViewTapListener((view, x, y) -> {
            if (getParentFragment() != null) {
                ((ImagePagerFragment) getParentFragment()).blo();
            }
        });

        String mineType = item.getMediaType();

        if (mineType.equals("image/jpg") || mineType.equals("image/jpeg") || mineType.equals("image/png")) {
            scaleImageView.resetScaleAndCenter();
            scaleImageView.setVisibility(View.VISIBLE);
            scaleImageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
            scaleImageView.setDoubleTapZoomScale(1.2f);
            scaleImageView.setDoubleTapZoomDuration(400);
            scaleImageView.setImage(ImageSource.uri(item.getPathMediaItem()));
//            scaleImageView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
//                public void onSwipeTop() {
//                    if (getFragmentManager() != null) {
//                        fragment.setMediaItem(finalItem);
//                        fragment.show(getFragmentManager(), fragment.getTag());
//                    }
//                }
//                public void onSwipeBottom() {
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
//
//                @Override
//                public void onSingleTouch() {
//                    Log.v("PictureFragment", "scaleImageView: onSingleTouch");
//                    if (getParentFragment() != null) {
//                        ((ImagePagerFragment )getParentFragment()).blo();
//                    }
//                }
//            });
        }
        else {
            photoView.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(item.getPathMediaItem())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                                target, boolean isFirstResource) {
                            spinKitView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            spinKitView.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into((ImageView) v.findViewById(R.id.ivPhotoView));
        }
        ivPlay.setVisibility(item.getMediaType().contains("video") ? View.VISIBLE : View.GONE);
        String mediaItemPath = item.getPathMediaItem();
        ivPlay.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mediaItemPath));
            intent.setDataAndType(Uri.parse(mediaItemPath), "video/*");
            startActivity(intent);
        });
        return v;
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onImageLoaded() {
        spinKitView.setVisibility(View.GONE);
        Objects.requireNonNull(getParentFragment()).startPostponedEnterTransition();
    }

    @Override
    public void onPreviewLoadError(Exception e) {

    }

    @Override
    public void onImageLoadError(Exception e) {

    }

    @Override
    public void onTileLoadError(Exception e) {

    }

    @Override
    public void onPreviewReleased() {

    }

    @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivSubSamplingView:
                    if (getParentFragment() != null) {
                        ((ImagePagerFragment) getParentFragment()).blo();
                    }
                    break;
            }
    }

    @Override
    public void onScaleChanged(float scale, int origin) {
        viewPager.setSwipeLocked(scale > scaleImageView.getMinScale());
    }

    @Override
    public void onCenterChanged(PointF pointF, int i) {

    }
}
