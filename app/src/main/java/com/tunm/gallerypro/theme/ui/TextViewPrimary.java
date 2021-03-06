package com.tunm.gallerypro.theme.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.tunm.gallerypro.CustomModelClass;
import com.tunm.gallerypro.setting.callback.ThemeChangeObserver;
import com.tunm.gallerypro.R;
import com.tunm.gallerypro.theme.ColorTheme;

public class TextViewPrimary extends android.support.v7.widget.AppCompatTextView implements ThemeChangeObserver {
    private ColorTheme colorTheme;

    public TextViewPrimary(Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
        init();
    }

    public TextViewPrimary(Context context, AttributeSet attrs) {
        super(context, attrs);
        colorTheme = new ColorTheme(context);
        init();
    }

    public TextViewPrimary(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorTheme = new ColorTheme(context);
        init();
    }

    private void init() {
        if(colorTheme.isDarkTheme()) {
             this.setTextColor(getContext().getColor(R.color.colorDarkAccent));
        } else {
            setTextColor(colorTheme.getAccentColor());
        }
        CustomModelClass.getInstance().addThemeChangeObserver(this);
    }

    @Override
    public void requestUpdateTheme() {
        if(colorTheme.isDarkTheme()) {
            this.setTextColor(getContext().getColor(R.color.colorDarkAccent));
        } else {
            setTextColor(colorTheme.getAccentColor());
        }
    }
}
