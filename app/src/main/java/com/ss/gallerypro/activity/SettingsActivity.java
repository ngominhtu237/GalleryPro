package com.ss.gallerypro.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.ss.gallerypro.BaseActivity;
import com.ss.gallerypro.CustomModelClass;
import com.ss.gallerypro.R;
import com.ss.gallerypro.theme.ui.TextViewPrimary;
import com.ss.gallerypro.utils.CommonStatusBarColor;
import com.ss.gallerypro.view.SwitchButton;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rootView)
    RelativeLayout mRelativeLayout;

    @BindView(R.id.cardView1)
    CardView cardView1;

    @BindView(R.id.cardView2)
    CardView cardView2;
    @BindView(R.id.settings_theme_title) TextViewPrimary themeTitle;


    @BindView(R.id.settings_theme_icon) ImageView themeIc;
    @BindView(R.id.settings_night_mode_icon) ImageView nightModeIc;
    @BindView(R.id.settings_column_icon) ImageView columnIc;
    @BindView(R.id.settings_language_icon) ImageView languageIc;

    @BindView(R.id.settings_night_mode_switch)
    SwitchButton nightModeSwitch;

    @BindView(R.id.settings_theme_container) RelativeLayout settingsThemeContainer;
    @BindView(R.id.settings_night_mode_container) RelativeLayout settingsNightModeContainer;
    @BindView(R.id.settings_column_container) RelativeLayout settingsColumnContainer;
    @BindView(R.id.settings_language_container) RelativeLayout settingsLanguageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");

        nightModeSwitch.setCheckedColor(getColor(R.color.colorDarkHighlight));

        if(mColorTheme.isDarkTheme()) {
            nightModeSwitch.setChecked(true);
        } else {
            nightModeSwitch.setChecked(false);
        }

        registerListener();

        refreshTheme();
    }

    private void registerListener() {
        settingsThemeContainer.setOnClickListener(this);
        nightModeSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                changeSwitch(isChecked);
            }
        });
    }

    private void changeSwitch(boolean isChecked) {
        nightModeSwitch.setChecked(isChecked);
        mColorTheme.setDarkTheme(isChecked);
        refreshTheme();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_theme_container:
                if(mColorTheme.isDarkTheme()) {
                    Toast.makeText(this, "You can't select this option in night mode!", Toast.LENGTH_SHORT).show();
                } else {
                    showColorPicker();
                }
        }
    }

    private void showColorPicker() {
        int initColor = mColorTheme.getPrimaryColor();
        ColorPickerDialogBuilder
                .with(this, R.style.ColorPickerDialogTheme)
                .setTitle("Choose color")
                .initialColor(initColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .showColorPreview(true)
                .setPositiveButton("set", (dialog, selectedColor, allColors) -> {
                    mColorTheme.setPrimaryColor(selectedColor);
                    refreshTheme();
                })
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .build().show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    private void refreshTheme() {
        nightModeSwitch.setShowIndicator(false);

        if(mColorTheme.isDarkTheme()) {
            int colorBg = getColor(R.color.colorDarkBackground);
            int colorDarkBgHighlight = getColor(R.color.colorDarkBackgroundHighlight);
            mRelativeLayout.setBackgroundColor(colorBg);
            cardView1.setCardBackgroundColor(colorDarkBgHighlight);
            cardView2.setCardBackgroundColor(colorDarkBgHighlight);

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorBg));
            CommonStatusBarColor.setStatusBarColor(this, colorBg);

            themeIc.setImageResource(R.mipmap.settings_theme_white_96);
            nightModeIc.setImageResource(R.mipmap.setting_nightmode_white_96);
            columnIc.setImageResource(R.mipmap.setting_column_white_96);
        } else {
            int colorBg = mColorTheme.getBackgroundColor();
            int colorBgHighlight = mColorTheme.getBackgroundHighLightColor();
            mRelativeLayout.setBackgroundColor(colorBgHighlight);
            cardView1.setCardBackgroundColor(colorBg);
            cardView2.setCardBackgroundColor(colorBg);

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mColorTheme.getPrimaryColor()));
            CommonStatusBarColor.setStatusBarColor(this, mColorTheme.getPrimaryColor());

            themeIc.setImageResource(R.mipmap.settings_theme_black_96);
            nightModeIc.setImageResource(R.mipmap.setting_nightmode_black_96);
            columnIc.setImageResource(R.mipmap.setting_column_black_96);

        }
        CustomModelClass.getInstance().changeState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
