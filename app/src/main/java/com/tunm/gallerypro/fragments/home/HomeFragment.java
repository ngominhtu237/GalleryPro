package com.tunm.gallerypro.fragments.home;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.tunm.gallerypro.CustomModelClass;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.fragments.BaseFragment;
import com.tunm.gallerypro.fragments.list.normal.albums.AlbumsFragment;
import com.tunm.gallerypro.fragments.list.section.timeline.TimelineFragment;
import com.tunm.gallerypro.fragments.list.section.video.VideoFragment;
import com.tunm.gallerypro.theme.SystemUI;
import com.tunm.gallerypro.utils.ViewSizeUtils;
import com.tunm.gallerypro.view.LockableViewPager;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.appBar)
    AppBarLayout appBarLayout;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.homeViewPager)
    LockableViewPager viewPager;

    private View rootView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupTabLayout();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        requestUpdateTheme();
        CustomModelClass.getInstance().addThemeChangeObserver(this);
        SystemUI.showNavigationBar(mActivity, getView());

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rootView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin + ViewSizeUtils.getStatusBarHeight(mActivity), params.rightMargin, params.bottomMargin + ViewSizeUtils.getNavigationBarHeight(mActivity));
        rootView.setLayoutParams(params);
        return rootView;
    }

    private void setupTabLayout() {
        appBarLayout.setElevation(0);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabLayout.setSelectedTabIndicatorHeight((int) getResources().getDimension(R.dimen.tab_indicator_height));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new TimelineFragment(), "Pictures");
        adapter.addFragment(new VideoFragment(), "Videos");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    public void hideAppBarLayout() {
        if(appBarLayout != null) {
            translateAppbar(rootView, -appBarLayout.getHeight(), 300);
        }
    }

    public void showAppBarLayout() {
        if(appBarLayout != null) {
            translateAppbar(rootView, appBarLayout.getHeight(), 300);
        }
    }

    public void translateAppbar(View view, int delta, int duration){
        TranslateAnimation animate = new TranslateAnimation(
                0,                // fromXDelta
                0,                // toXDelta
                0,             // fromYDelta
                delta);                // toYDelta
        animate.setDuration(duration);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.topMargin += delta;
                view.setLayoutParams(params);
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    @Override
    public void requestUpdateTheme() {
        if(tabLayout != null) {
            if (mColorTheme.isDarkTheme()) {
                int colorBg = mActivity.getColor(R.color.colorDarkBackgroundHighlight);
                tabLayout.setBackgroundColor(colorBg);
            } else {
                tabLayout.setBackgroundColor(mColorTheme.getPrimaryColor());
            }
        }
    }
}
