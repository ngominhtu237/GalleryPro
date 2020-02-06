package com.ss.gallerypro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ss.gallerypro.setting.callback.ThemeChangeObserver;
import com.ss.gallerypro.theme.ColorTheme;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements ThemeChangeObserver {

    protected ColorTheme mColorTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mColorTheme = new ColorTheme(this);
    }

    protected abstract int getLayoutId();

    @Override
    public void requestUpdateTheme() {

    }
}
