package com.tunm.gallerypro.theme.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.tunm.gallerypro.R;
import com.tunm.gallerypro.theme.ColorTheme;

public class TitleTextView extends android.support.v7.widget.AppCompatTextView {
    private ColorTheme colorTheme;

    public TitleTextView(Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
        init();
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        colorTheme = new ColorTheme(context);
        init();
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorTheme = new ColorTheme(context);
        init();
    }

    private void init() {
        if(colorTheme.isDarkTheme()) {
            setTextColor(getContext().getColor(R.color.colorDarkAccent));
        } else {
            setTextColor(colorTheme.getPrimaryColor());
        }
    }
}
