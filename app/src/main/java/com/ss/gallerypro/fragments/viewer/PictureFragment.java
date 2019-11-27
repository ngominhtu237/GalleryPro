package com.ss.gallerypro.fragments.viewer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.ss.gallerypro.DrawerLocker;
import com.ss.gallerypro.R;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.utils.preferences.Prefs;
import com.ss.gallerypro.view.dialog.FragmentBottomSheetDialog;
import com.ss.gallerypro.view.OnSwipeTouchListener;

import java.util.Objects;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment implements SubsamplingScaleImageView.OnImageEventListener, View.OnClickListener, PhotoViewAttacher.OnViewTapListener {

    private static final String KEY_IMAGE_OBJ = "com.ss.key.imageObj";
    private ProgressBar progressBar;

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
        SubsamplingScaleImageView scaleImageView = v.findViewById(R.id.ivSubSamplingView);
        ImageView ivPlay = v.findViewById(R.id.ivPlayIcon);
        progressBar = v.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Prefs.getPrimaryColor(getActivity()), PorterDuff.Mode.SRC_IN );

        scaleImageView.setOnImageEventListener(this);

        MediaItem item = null;
        if (getArguments() != null) {
            item = (MediaItem) getArguments().getSerializable(KEY_IMAGE_OBJ);
        }

        assert item != null;
        v.findViewById(R.id.ivPhotoView).setTransitionName(item.getName());
        Log.v("transition receive name ", item.getName());

        PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
        mAttacher.setOnViewTapListener(this);
        scaleImageView.setOnClickListener(this);

        String mineType = item.getMediaType();
        FragmentBottomSheetDialog fragment = new FragmentBottomSheetDialog();
        MediaItem finalItem = item;

        if (mineType.equals("image/jpg") || mineType.equals("image/jpeg") || mineType.equals("image/png")) {
            scaleImageView.setVisibility(View.VISIBLE);
            scaleImageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
            scaleImageView.setImage(ImageSource.uri(item.getPathMediaItem()));
            scaleImageView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                public void onSwipeTop() {
                    if (getFragmentManager() != null) {
                        fragment.setMediaItem(finalItem);
                        fragment.show(getFragmentManager(), fragment.getTag());
                    }
                }
                public void onSwipeBottom() {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        } else {
            photoView.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(item.getPathMediaItem())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                                target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            Objects.requireNonNull(getParentFragment()).startPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            Objects.requireNonNull(getParentFragment()).startPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into((ImageView) v.findViewById(R.id.ivPhotoView));
            photoView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                public void onSwipeTop() {
                    FragmentBottomSheetDialog fragment = new FragmentBottomSheetDialog();
                    if (getFragmentManager() != null) {
                        fragment.setMediaItem(finalItem);
                        fragment.show(getFragmentManager(), fragment.getTag());
                    }
                }
                public void onSwipeBottom() {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
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
        progressBar.setVisibility(View.GONE);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivSubSamplingView:
                if (getParentFragment() != null) {
                    ((ImagePagerFragment )getParentFragment()).blo();
                }
                break;
        }
    }

    @Override
    public void onViewTap(View view, float v, float v1) {
        if (getParentFragment() != null) {
            ((ImagePagerFragment )getParentFragment()).blo();
        }
    }
}
