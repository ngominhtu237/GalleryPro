package com.ss.gallerypro.fragments.viewer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    ImagePagerAdapter(Fragment fragment) {
        super(fragment.getChildFragmentManager());
    }

    @Override
    public int getCount() {
        return ImagePagerFragment.mImageList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return PictureFragment.newInstance(ImagePagerFragment.mImageList.get(position));
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}
