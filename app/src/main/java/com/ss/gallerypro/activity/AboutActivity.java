package com.ss.gallerypro.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ss.gallerypro.R;
import com.ss.gallerypro.constant.Constant;
import com.ss.gallerypro.theme.ColorTheme;
import com.ss.gallerypro.utils.CommonBarColor;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    protected ColorTheme mColorTheme;
    private ScrollView rootView;
    private MaterialIconView emailIcon, googlePlayIcon, instagramIcon;
    private LinearLayout contactUsContainer, googlePlayContainer, instagramContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mColorTheme = new ColorTheme(this);
        findView();

        contactUsContainer.setOnClickListener(this);
        googlePlayContainer.setOnClickListener(this);
        instagramContainer.setOnClickListener(this);

        refreshTheme();
    }

    private void findView() {
        rootView = findViewById(R.id.rootView);
        emailIcon = findViewById(R.id.email_icon);
        googlePlayIcon = findViewById(R.id.google_play_icon);
        instagramIcon = findViewById(R.id.instagram_icon);
        contactUsContainer = findViewById(R.id.contact_us_container);
        googlePlayContainer = findViewById(R.id.google_play_container);
        instagramContainer = findViewById(R.id.instagram_container);
    }

    private void refreshTheme() {
        int darkColor = getColor(R.color.colorDarkBackground);
        int lightColor = getColor(R.color.accent_white);
        if(mColorTheme.isDarkTheme()) {
            CommonBarColor.setStatusBarColor(this, darkColor);
            rootView.setBackgroundColor(darkColor);
            emailIcon.setColor(lightColor);
            googlePlayIcon.setColor(lightColor);
            instagramIcon.setColor(lightColor);
        } else {
            CommonBarColor.setStatusBarColor(this, lightColor);
            rootView.setBackgroundColor(lightColor);
            emailIcon.setColor(darkColor);
            googlePlayIcon.setColor(darkColor);
            instagramIcon.setColor(darkColor);
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_us_container:
                startEmail();
                break;
            case R.id.google_play_container:
                goToMarket();
                break;
            case R.id.instagram_container:
                goToInstagram();
                break;
        }
    }

    private void goToInstagram() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://instagram.com/_u/" + Constant.INSTAGRAM_ID));

        if (isAppInstalled(this, "com.instagram.android")) {
            intent.setPackage("com.instagram.android");
        }
        startActivity(intent);
    }

    private void goToMarket() {
        Uri uri = Uri.parse("market://details?id=" + Constant.PACKAGE_APP_ID);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void startEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constant.EMAIL});
        startActivity(intent);
    }

    static Boolean isAppInstalled(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(appName)) {
                installed = true;
                break;
            }
        }

        return installed;
    }
}
