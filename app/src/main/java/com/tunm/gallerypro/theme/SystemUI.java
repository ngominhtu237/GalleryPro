package com.tunm.gallerypro.theme;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tunm.gallerypro.R;

public class SystemUI {
    private static final int FULL_SCREEN_UI_FLAG = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

    private static final int FULL_SCREEN_UI_WITH_HIDE_NAVIGATION_FLAG = FULL_SCREEN_UI_FLAG
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    public static void showNavigationBar(Activity activity, View view) {
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        changeScreenMode(activity, view, visibility, false);
    }

    public static void hideNavigationBar(Activity activity, View view) {
        changeScreenMode(activity, view, FULL_SCREEN_UI_WITH_HIDE_NAVIGATION_FLAG, false);
    }

    private static void changeScreenMode(Activity activity, View view, int visibility, boolean lightStatus) {
        try {
            Window window = activity.getWindow();
//            int osdColor = activity.getColor(R.color.transparent);
//            if (true) {
//                window.setNavigationBarColor(osdColor);
//            }
            if( lightStatus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    visibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                }
                visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    visibility &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                }
                visibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }

//            window.setStatusBarColor(osdColor);


            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) { //full-screen
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            window.getDecorView().setSystemUiVisibility(visibility);
            if (view != null) {
                view.requestApplyInsets();
            }
        } catch (Exception e) {
            Log.e("SVMC", "changeScreenMode failed e=" + e.getMessage());
        }
    }
}
