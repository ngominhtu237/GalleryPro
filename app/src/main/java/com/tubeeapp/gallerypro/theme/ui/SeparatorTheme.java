package com.tubeeapp.gallerypro.theme.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.theme.ColorTheme;

public class SeparatorTheme extends View {

    private ColorTheme colorTheme;

    public SeparatorTheme(Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
        init();
    }

    public SeparatorTheme(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        colorTheme = new ColorTheme(context);
        init();
    }

    public SeparatorTheme(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorTheme = new ColorTheme(context);
        init();
    }

    private void init() {
        if(colorTheme.isDarkTheme()) {
            setBackgroundColor(getContext().getColor(R.color.colorDarkPrimaryHighlight));
        } else {
            setBackgroundColor(Color.parseColor("#e1e1e1"));
        }
    }
}
