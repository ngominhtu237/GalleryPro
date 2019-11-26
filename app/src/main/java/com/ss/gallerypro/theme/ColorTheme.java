package com.ss.gallerypro.theme;

import android.content.Context;

import com.ss.gallerypro.utils.preferences.Prefs;

public class ColorTheme {
    private int mPrimaryColor;
    private int mAccentColor;
    private int mHighLightColor;
    private int mBackgroundColor;

    private int mPrimaryHighLightColor;
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

    public int getHighLightColor() {
        return Prefs.getHighlightColor(mContext);
    }

    public void setHighLightColor(int color) {
        Prefs.setHighlightColor(color);
    }

    public int getPrimaryHighLightColor() {
        return Prefs.getPrimaryHighlightColor(mContext);
    }

    public void setPrimaryHighLightColor(int color) {
        Prefs.setPrimaryHighlightColor(color);
    }
}
