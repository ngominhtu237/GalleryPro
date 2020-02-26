package com.tunm.gallerypro.theme;

import android.content.Context;

import com.tunm.gallerypro.utils.preferences.Prefs;

public class ColorTheme {
    private int mPrimaryColor;
    private int mAccentColor;
    private int mHighLightColor;
    private int mBackgroundColor;

    private int mPrimaryHighLightColor;
    private boolean isDarkTheme;
    private Context mContext;

    public ColorTheme(Context context) {
        mContext = context;
    }

    public int getPrimaryColor() {
        return Prefs.getPrimaryColor(mContext);
    }

    public void setPrimaryColor(int color) {
        Prefs.setPrimaryColor(color);
    }

    public int getAccentColor() {
        return Prefs.getAccentColor(mContext);
    }

    public void setAccentColor(int color) {
        Prefs.setAccentColor(color);
    }

    public int getBackgroundColor() {
        return Prefs.getBackgroundColor(mContext);
    }

    public void setBackgroundColor(int color) {
        Prefs.setBackgroundColor(color);
    }

    public int getBackgroundHighLightColor() {
        return Prefs.getBackgroundHighlightColor(mContext);
    }

    public void setBackgroundHighLightColor(int color) {
        Prefs.setBackgroundHighlightColor(color);
    }

    public int getAccentHighLightColor() {
        return Prefs.getAccentHighlightColor(mContext);
    }

    public void setAccentHighLightColor(int color) {
        Prefs.setAccentHighlightColor(color);
    }

    public boolean isDarkTheme() {
        return Prefs.getIsDarkTheme(mContext);
    }

    public void setDarkTheme(boolean isDarkTheme) {
        Prefs.setIsDarkTheme(isDarkTheme);
    }
}
