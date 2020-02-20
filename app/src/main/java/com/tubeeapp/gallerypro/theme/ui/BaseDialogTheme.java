package com.tubeeapp.gallerypro.theme.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.theme.ColorTheme;

import java.util.Objects;

public abstract class BaseDialogTheme extends AlertDialog {
    protected ColorTheme mColorTheme;
    protected Context mContext;

    public BaseDialogTheme(@NonNull Context context) {
        super(context);
        mContext = context;
        mColorTheme = new ColorTheme(mContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setDialogWidth();
    }

    private void setDialogWidth() {
        if(getWidth() != -1) {
            int w = (int) (mContext.getResources().getDisplayMetrics().widthPixels * getWidth());
            Objects.requireNonNull(getWindow()).setLayout(w, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    protected float getWidth() {
        return -1;
    }

    protected void refreshTheme() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(mColorTheme.isDarkTheme() ? getContext().getColor(R.color.colorDarkPrimary) : mColorTheme.getBackgroundColor());
        gd.setCornerRadius(25);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(gd);
    }

    protected abstract int getLayoutId();
}
