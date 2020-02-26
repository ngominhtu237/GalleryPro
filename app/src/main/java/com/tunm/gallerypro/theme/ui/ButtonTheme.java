package com.tunm.gallerypro.theme.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.tunm.gallerypro.R;
import com.tunm.gallerypro.theme.ColorTheme;

public class ButtonTheme extends android.support.v7.widget.AppCompatButton {
    private ColorTheme colorTheme;

    public ButtonTheme(Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
        init();
    }

    public ButtonTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
        colorTheme = new ColorTheme(context);
        init();
    }

    public ButtonTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorTheme = new ColorTheme(context);
        init();
    }

    private void init() {
        setBackground(null);
        if(colorTheme.isDarkTheme()) {
            setTextColor(getContext().getColor(R.color.colorDarkAccent));
        } else {
            setTextColor(colorTheme.getPrimaryColor());
        }
    }
}
