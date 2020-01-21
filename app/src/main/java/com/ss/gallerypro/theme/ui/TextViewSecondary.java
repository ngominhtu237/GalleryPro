package com.ss.gallerypro.theme.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.ss.gallerypro.CustomModelClass;
import com.ss.gallerypro.OnChooseColorListener;
import com.ss.gallerypro.R;
import com.ss.gallerypro.theme.ColorTheme;

public class TextViewSecondary extends android.support.v7.widget.AppCompatTextView implements OnChooseColorListener {
    private ColorTheme colorTheme;

    public TextViewSecondary(Context context) {
        super(context);
        colorTheme = new ColorTheme(context);
        init();
    }

    public TextViewSecondary(Context context, AttributeSet attrs) {
        super(context, attrs);
        colorTheme = new ColorTheme(context);
        init();
    }

    public TextViewSecondary(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        colorTheme = new ColorTheme(context);
        init();
    }

    private void init() {
        if(colorTheme.isDarkTheme()) {
            setTextColor(getContext().getColor(R.color.colorDarkHighlight));
        } else {
            setTextColor(colorTheme.getAccentHighLightColor());
        }
        CustomModelClass.getInstance().setListener(this);
    }

    @Override
    public void requestUpdateTheme() {
        if(colorTheme.isDarkTheme()) {
            setTextColor(getContext().getColor(R.color.colorDarkHighlight));
        } else {
            setTextColor(colorTheme.getAccentHighLightColor());
        }
    }
}