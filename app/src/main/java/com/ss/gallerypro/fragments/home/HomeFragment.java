package com.ss.gallerypro.fragments.home;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.gallerypro.CustomModelClass;
import com.ss.gallerypro.R;
import com.ss.gallerypro.fragments.BaseFragment;
import com.ss.gallerypro.fragments.list.normal.albums.AlbumsFragment;
import com.ss.gallerypro.fragments.list.section.timeline.TimelineFragment;
import com.ss.gallerypro.fragments.list.section.video.VideoFragment;

import java.util.Objects;

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
    ViewPager viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupTabLayout();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        requestUpdateTheme();
        CustomModelClass.getInstance().setListener(this);
        return view;
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
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        lp.height = 0;
        appBarLayout.setLayoutParams(lp);
    }

    public void showAppBarLayout() {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        lp.height = getHeightActionBar();
        appBarLayout.setLayoutParams(lp);
    }

    private int getHeightActionBar() {
        TypedValue tv = new TypedValue();
        if (Objects.requireNonNull(getActivity()).getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return 0;
    }

    @Override
    public void requestUpdateTheme() {
        if(tabLayout != null) {
            if (mColorTheme.isDarkTheme()) {
                int colorBg = getActivity().getColor(R.color.colorDarkBackgroundHighlight);
                tabLayout.setBackgroundColor(colorBg);
            } else {
                tabLayout.setBackgroundColor(mColorTheme.getPrimaryColor());
            }
        }
    }
}
