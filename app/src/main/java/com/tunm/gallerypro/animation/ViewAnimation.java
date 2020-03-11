package com.tunm.gallerypro.animation;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class ViewAnimation {

    // slide the rootView from its current position to below itself
    public static void slideDown(View view, int delta, int duration){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                delta); // toYDelta
        animate.setDuration(duration);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void slideUp(View view, int delta, int duration){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                delta,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(duration);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
}
