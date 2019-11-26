package com.ss.gallerypro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.ss.gallerypro.activity.AboutActivity;
import com.ss.gallerypro.data.StatisticModel;
import com.ss.gallerypro.data.filter.MediaFilter;
import com.ss.gallerypro.data.provider.CPHelper;
import com.ss.gallerypro.fragments.home.HomeFragment;
import com.ss.gallerypro.theme.ColorTheme;

import static com.ss.gallerypro.data.utils.DataUtils.readableFileSize;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLocker, CallBackToActivityListener {

    private static final String TAG = "MainActivity";
    public static final int ABOUT_REQUEST_CODE = 1;
    private DrawerLayout drawer;
    private HeaderViewHolder mHeaderViewHolder;
    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle toggle;

    private static int lastClicked = -1;
    private ColorTheme colorTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorTheme = new ColorTheme(this);

        setToolbarCustom();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorTheme.getPrimaryColor());
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

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
}
