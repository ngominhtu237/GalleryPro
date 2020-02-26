package com.tunm.gallerypro.constant;

import android.view.View;

public class SystemUI {
    public static final int FULL_SCREEN_UI_FLAG = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

    public static final int FULL_SCREEN_UI_WITH_HIDE_NAVIGATION_FLAG = FULL_SCREEN_UI_FLAG
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE;
}
