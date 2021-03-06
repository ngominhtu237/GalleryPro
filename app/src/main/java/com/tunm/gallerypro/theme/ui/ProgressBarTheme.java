package com.tunm.gallerypro.theme.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.tunm.gallerypro.CustomModelClass;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.setting.callback.ThemeChangeObserver;
import com.tunm.gallerypro.theme.ColorTheme;

public class ProgressBarTheme extends ProgressBar implements ThemeChangeObserver {
    private ColorTheme colorTheme;

    public ProgressBarTheme(Context context) {
        super(context);
        init(context);
    }

    public ProgressBarTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressBarTheme(Context context, AttributeSet attrs, int defStyleAttr) {
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

    private void setColor(int color) {
        getIndeterminateDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
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
