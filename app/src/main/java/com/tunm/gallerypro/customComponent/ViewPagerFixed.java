package com.tunm.gallerypro.customComponent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tunm.gallerypro.view.LockableViewPager;

// Fix PhotoView lib: IllegalArgumentException (pointerIndex out of range) while using many fingers to zoom in and out

public class ViewPagerFixed extends LockableViewPager {
    public ViewPagerFixed(@NonNull Context context) {
        super(context);
    }

    public ViewPagerFixed(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
