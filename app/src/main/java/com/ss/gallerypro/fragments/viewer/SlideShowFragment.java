package com.ss.gallerypro.fragments.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ss.gallerypro.R;
import com.ss.gallerypro.activity.PicturePreview;
import com.ss.gallerypro.adapter.ImageViewPagerAdapter;
import com.ss.gallerypro.animation.DepthPageTransformer;
import com.ss.gallerypro.customComponent.ViewPagerFixed;
import com.ss.gallerypro.data.MediaItem;

import java.util.ArrayList;

public class SlideShowFragment extends Fragment {

    private ArrayList<MediaItem> mImageList;
    private ViewPagerFixed mViewPager;
    int selectedImagePosition;

    public SlideShowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent receivedIntent = getActivity().getIntent();
        selectedImagePosition = receivedIntent.getIntExtra("current_image_position", 0);
        mImageList = PicturePreview.mImageList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_slide_show, container, false);
        mViewPager = view.findViewById(R.id.viewPager);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        ImageViewPagerAdapter viewPagerAdapter = new ImageViewPagerAdapter(getActivity(), mImageList);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setPageMargin(10);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentImage(selectedImagePosition);
        return view;
    }

    private void setCurrentImage(int selectedImagePosition) {
        mViewPager.setCurrentItem(selectedImagePosition, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
