package com.tunm.gallerypro.theme.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import com.tunm.gallerypro.R;
import com.tunm.gallerypro.theme.ColorTheme;

public class RadioButtonTheme extends android.support.v7.widget.AppCompatRadioButton {
    private ColorTheme colorTheme;
    public RadioButtonTheme(Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
        init();
    }

    public RadioButtonTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
        colorTheme = new ColorTheme(context);
        init();
    }

    public RadioButtonTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorTheme = new ColorTheme(context);
        init();
    }

    private void init() {
        setBackground(null);
        if(colorTheme.isDarkTheme()) {
            setTextColor(getContext().getColor(R.color.colorDarkAccent));
            setButtonTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorDarkAccent)));
        } else {
            // setTextColor(colorTheme.getPrimaryColor());
        }
    }

}
