package com.tunm.gallerypro.animation;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                if (position > -1 && position < 1) {
                    float childWidth = ((ViewGroup) view).getChildAt(i).getWidth();
                    ((ViewGroup) view).getChildAt(i).setTranslationX(-(position * childWidth * 0.5f));
                }
            }
        }
    }
}