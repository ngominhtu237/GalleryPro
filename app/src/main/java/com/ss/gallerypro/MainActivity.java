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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ss.gallerypro.activity.AboutActivity;
import com.ss.gallerypro.fragments.home.HomeFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLocker {

    public static final int ABOUT_REQUEST_CODE = 1;
    private DrawerLayout drawer;
    private HeaderViewHolder mHeaderViewHolder;
    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle toggle;

    private static int lastClicked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // change navigation selected item on fragment backstack change
                Fragment current = getCurrentFragment();
//                if (current instanceof VideosFragment) {
//                    navigationView.setCheckedItem(R.id.nav_videos);
//                } else if (current instanceof AboutUsFragment){
//                    navigationView.setCheckedItem(R.id.nav_about_us);
//                } else {
//                    navigationView.setCheckedItem(R.id.nav_albums);
//                }
                // handler hamburger to arrow and reverse
                if (mFragmentManager.getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    //show hamburger
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.syncState();
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });

        if (savedInstanceState == null) {
            startHomeFragment();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        lastClicked = getCheckedItem(navigationView);
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
    }


    @Override
    public void onBackPressed() {
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().show();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
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
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);
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
}
