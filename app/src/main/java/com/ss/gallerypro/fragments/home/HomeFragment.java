package com.ss.gallerypro.fragments.home;


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

import com.ss.gallerypro.R;
import com.ss.gallerypro.fragments.BaseFragment;
import com.ss.gallerypro.fragments.list.TimelineFragment;
import com.ss.gallerypro.fragments.list.albums.album.AlbumsFragment;
import com.ss.gallerypro.fragments.list.split.video.VideosFragment;

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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new TimelineFragment(), "Pictures");
        adapter.addFragment(new AlbumsFragment(), "Albums");
        adapter.addFragment(new VideosFragment(), "Videos");
        viewPager.setAdapter(adapter);
    }
}
