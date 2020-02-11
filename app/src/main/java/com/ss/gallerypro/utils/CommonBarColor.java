package com.ss.gallerypro.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.view.ActionMode;
import android.support.v7.view.StandaloneActionMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class CommonBarColor {
    @SuppressLint("ObsoleteSdkInt")
    public static void setStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public static void setNavigationBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        window.setNavigationBarColor(color);
    }

    public static void setActionModeColor(Context context, int color) {
        try {
            int amId = context.getResources().getIdentifier("action_context_bar", "id", "android");
            View view = ((Activity) context).findViewById(amId);
            view.setBackgroundColor(color);
        } catch (Exception ignored) {
        }
    }

    public static void setActionModeBackgroundColor(ActionMode actionMode, int color) {
        try {
            StandaloneActionMode standaloneActionMode = (StandaloneActionMode) actionMode;
            Field mContextView = StandaloneActionMode.class.getDeclaredField("mContextView");
            mContextView.setAccessible(true);
            Object value = mContextView.get(standaloneActionMode);
            ((View) value).setBackground(new ColorDrawable(color));
        } catch (Throwable ignore) {
        }
    }
}
