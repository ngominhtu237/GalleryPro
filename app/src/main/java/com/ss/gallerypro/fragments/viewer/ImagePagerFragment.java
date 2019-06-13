package com.ss.gallerypro.fragments.viewer;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ss.gallerypro.DrawerLocker;
import com.ss.gallerypro.R;
import com.ss.gallerypro.animation.ParallaxPageTransformer;
import com.ss.gallerypro.customComponent.ViewPagerFixed;
import com.ss.gallerypro.data.MediaItem;
import com.ss.gallerypro.utils.Convert;
import com.ss.gallerypro.utils.ViewSizeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ImagePagerFragment extends Fragment {

    public static ArrayList<MediaItem> mImageList;
    private ViewPagerFixed mViewPager;
    private View view;
    int currentPosition;

    public ImagePagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            currentPosition = getArguments().getInt("currentPosition");
        }

        ((DrawerLocker) Objects.requireNonNull(getActivity())).setDrawerEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setToolbar(true);

        view = inflater.inflate(R.layout.image_pager_fragment, container, false);
        mViewPager = view.findViewById(R.id.viewPager);
        mViewPager.setPageTransformer(true, new ParallaxPageTransformer());
        mViewPager.setAdapter(new ImagePagerAdapter(this));
        mViewPager.setPageMargin(10);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        TextView tvDate = view.findViewById(R.id.tvDate);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvDate.getLayoutParams();
        params.setMargins(0,  0, 0, ViewSizeUtils.getNavigationBarHeight(getActivity()));
        tvDate.setLayoutParams(params);

        setCurrentImage(currentPosition);

        showBottomView(currentPosition);

        prepareSharedElementTransition();
        postponeEnterTransition();

        return view;
    }

    private void showBottomView(int currentPos) {
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvDate = view.findViewById(R.id.tvDate);
        tvName.setText(mImageList.get(currentPos).getName());
        tvDate.setText(Convert.Epoch2DateString(Long.parseLong(mImageList.get(currentPos).getDateTaken())));
    }

    private void prepareSharedElementTransition() {
        Transition transition = TransitionInflater.from(getContext()).inflateTransition(R.transition.image_shared_element_transition);
        setSharedElementEnterTransition(transition);

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        Log.v("callback", "onMapSharedElements");
                    }
                });
    }

    private void setCurrentImage(int selectedImagePosition) {
        mViewPager.setCurrentItem(selectedImagePosition, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            showBottomView(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public void onDestroy() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setToolbar(false);
        super.onDestroy();
    }

    private void setToolbar(boolean isMargin) {
        Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
        params.setMargins(0, isMargin ? ViewSizeUtils.getStatusBarHeight(getActivity()) : 0, 0, 0);
        toolbar.setLayoutParams(params);
    }


}
