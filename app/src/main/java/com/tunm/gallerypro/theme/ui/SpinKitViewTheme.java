package com.tunm.gallerypro.theme.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.github.ybq.android.spinkit.SpinKitView;
import com.tunm.gallerypro.CustomModelClass;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.setting.callback.ThemeChangeObserver;
import com.tunm.gallerypro.theme.ColorTheme;

public class SpinKitViewTheme extends SpinKitView implements ThemeChangeObserver {
    private ColorTheme colorTheme;
    public SpinKitViewTheme(Context context) {
        super(context);
        init(context);
    }

    public SpinKitViewTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpinKitViewTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        colorTheme = new ColorTheme(context);
        if(colorTheme.isDarkTheme()) {
            setColor(getResources().getColor(R.color.accent_white));
        } else {
            setColor(colorTheme.getPrimaryColor());
        }
        CustomModelClass.getInstance().addThemeChangeObserver(this);
    }

    @Override
    public void requestUpdateTheme() {
        if(colorTheme.isDarkTheme()) {
            setColor(getResources().getColor(R.color.accent_white));
        } else {
            setColor(colorTheme.getPrimaryColor());
        }
    }
}
