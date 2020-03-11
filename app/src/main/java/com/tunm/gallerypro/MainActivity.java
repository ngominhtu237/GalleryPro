package com.tunm.gallerypro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.tunm.gallerypro.activity.AboutActivity;
import com.tunm.gallerypro.activity.SettingsActivity;
import com.tunm.gallerypro.data.StatisticModel;
import com.tunm.gallerypro.data.filter.MediaFilter;
import com.tunm.gallerypro.data.provider.CPHelper;
import com.tunm.gallerypro.fragments.home.HomeFragment;
import com.tunm.gallerypro.setting.callback.ThemeChangeObserver;
import com.tunm.gallerypro.theme.ColorTheme;
import com.tunm.gallerypro.utils.CommonBarColor;

import static com.tunm.gallerypro.data.utils.DataUtils.readableFileSize;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLocker, CallBackToActivityListener , ThemeChangeObserver {

    private static final String TAG = "MainActivity";
    public static final int ABOUT_REQUEST_CODE = 1;
    public static final int SETTINGS_REQUEST_CODE = 2;
    private DrawerLayout drawer;
    private HeaderViewHolder mHeaderViewHolder;
    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Menu menu;

    private static int lastClicked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColorTheme = new ColorTheme(this);

        setToolbarCustom();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(mColorTheme.getPrimaryColor());
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();

        mHeaderViewHolder = new HeaderViewHolder(navigationView.getHeaderView(0));
        initNavHeader();

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.addOnBackStackChangedListener(() -> {
            // change navigation selected item on fragment backStack change
            Fragment current = getCurrentFragment();
            if (current instanceof HomeFragment) {
                setDrawerEnabled(true);
            }
            // handler hamburger to arrow and reverse
            if (mFragmentManager.getBackStackEntryCount() > 0) {
                toggle.setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            } else {
                //show hamburger
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                toggle.syncState();
                toolbar.setNavigationOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
            }
        });

        if (savedInstanceState == null) {
            startHomeFragment();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        lastClicked = getCheckedItem(navigationView);

        CustomModelClass.getInstance().addThemeChangeObserver(this);
        requestUpdateTheme();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void setToolbarCustom() {

    }

    private int getCheckedItem(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isChecked()) {
                return item.getItemId();
            }
        }
        return -1;
    }

    public Fragment getCurrentFragment() {
        return mFragmentManager.findFragmentById(R.id.fragment_container);
    }

    @SuppressLint("SetTextI18n")
    private void initNavHeader() {
        String left = getEmojiByUnicode(0x1F603) + getEmojiByUnicode(0x1F604);
        String right = getEmojiByUnicode(0x1F60D) + getEmojiByUnicode(0x1F618);
        mHeaderViewHolder.mTitle.setText(left + " Gallery " + right);
        getDataNavHeader();
    }

    private void getDataNavHeader() {
        StatisticModel image = CPHelper.getStatisticsImage(this, MediaFilter.IMAGE);
        mHeaderViewHolder.tvPhotoCount.setText(String.valueOf(image.getCount()));
        mHeaderViewHolder.tvPhotoSize.setText(readableFileSize(image.getSize()));

        StatisticModel video = CPHelper.getStatisticsImage(this, MediaFilter.VIDEO);
        mHeaderViewHolder.tvVideoCount.setText(String.valueOf(video.getCount()));
        mHeaderViewHolder.tvVideoSize.setText(readableFileSize(video.getSize()));

        StatisticModel album = CPHelper.getStatisticsAlbum(this);
        mHeaderViewHolder.tvAlbumCount.setText(String.valueOf(album.getCount()));
        mHeaderViewHolder.tvAlbumSize.setText(readableFileSize(album.getSize()));
    }


    @Override
    public void onBackPressed() {
        getSupportActionBar().setTitle(R.string.app_name);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int currentItem = item.getItemId();
        if(currentItem != lastClicked) {
            if(currentItem == R.id.nav_home) {
                HomeFragment homeFragment= new HomeFragment();
                mFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment,"HomeFragment")
                        .commit();
                lastClicked = currentItem;
            }

            if(currentItem == R.id.nav_settings) {
                    startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
                    return false;
                }

                if(currentItem == R.id.nav_about_us) {
                    startActivityForResult(new Intent(this, AboutActivity.class), ABOUT_REQUEST_CODE);
                    return false;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startHomeFragment() {
        HomeFragment homeFragment= new HomeFragment();
        mFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment,"HomeFragment")
                .commit();
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public void setDrawerEnabled(boolean enabled) {
        if(toggle != null) toggle.setDrawerIndicatorEnabled(enabled);
        drawer.setDrawerLockMode(enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ABOUT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
//                drawer.closeDrawer(Gravity.START, false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCallBack() {
        getDataNavHeader();
        Log.v(TAG, "refresh data header");
    }

    @Override
    public void requestUpdateTheme() {
        super.requestUpdateTheme();
        if(mHeaderViewHolder != null){
            mHeaderViewHolder.mNavHeaderBg.setBackgroundColor(mColorTheme.isDarkTheme() ? getColor(R.color.colorDarkBackgroundHighlight): mColorTheme.getPrimaryColor());
        }
        if(mColorTheme.isDarkTheme()) {
            int colorBg = getColor(R.color.colorDarkBackgroundHighlight);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorBg));
            CommonBarColor.setStatusBarColor(this, colorBg);
            CommonBarColor.setNavigationBarColor(this, colorBg);
            navigationView.setBackground(new ColorDrawable(getColor(R.color.md_dark_cards)));
            navigationView.setItemTextColor(ColorStateList.valueOf(getColor(R.color.colorDarkAccent)));
            navigationView.setItemBackground(getDrawable(R.drawable.drawer_item_dark));
            drawer.setBackground(new ColorDrawable(colorBg)); // prevent blink actionbar when back from detail view
            menu.findItem(R.id.nav_home).setIcon(R.mipmap.nav_home_dark);
            menu.findItem(R.id.nav_settings).setIcon(R.mipmap.nav_settings_dark);
            menu.findItem(R.id.nav_rate).setIcon(R.mipmap.nav_rate_dark);
            menu.findItem(R.id.nav_about_us).setIcon(R.mipmap.nav_about_dark);
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mColorTheme.getPrimaryColor()));
            CommonBarColor.setStatusBarColor(this, mColorTheme.getPrimaryColor());
            CommonBarColor.setNavigationBarColor(this, mColorTheme.getPrimaryColor());
            navigationView.setBackground(new ColorDrawable(getColor(R.color.colorBackground)));
            navigationView.setItemTextColor(ColorStateList.valueOf(getColor(R.color.colorAccent)));
            navigationView.setItemBackground(getDrawable(R.drawable.drawer_item_light));
            drawer.setBackground(new ColorDrawable(mColorTheme.getPrimaryColor()));
            menu.findItem(R.id.nav_home).setIcon(R.mipmap.nav_home);
            menu.findItem(R.id.nav_settings).setIcon(R.mipmap.nav_settings);
            menu.findItem(R.id.nav_rate).setIcon(R.mipmap.nav_rate);
            menu.findItem(R.id.nav_about_us).setIcon(R.mipmap.nav_about);
        }
    }
}
