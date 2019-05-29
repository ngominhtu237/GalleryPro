package com.ss.gallerypro;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.ss.gallerypro.fragments.AboutUsFragment;
import com.ss.gallerypro.fragments.list.albums.album.AlbumsFragment;
import com.ss.gallerypro.fragments.list.video.VideoFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private HeaderViewHolder mHeaderViewHolder;

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
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
            startAlbumView();
            navigationView.setCheckedItem(R.id.nav_albums);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initNavHeader() {
        String left = getEmojiByUnicode(0x1F603) + getEmojiByUnicode(0x1F604);
        String right = getEmojiByUnicode(0x1F60D) + getEmojiByUnicode(0x1F618);
        mHeaderViewHolder.mTitle.setText(left + " Gallery " + right);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_albums:
                AlbumsFragment albumsFragment = new AlbumsFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        //.replace(R.id.fragment_container, new AlbumsFragment())
                        .add(R.id.fragment_container, albumsFragment, "AlbumsFragment")
                        .commit();
                break;

            case R.id.nav_videos:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideoFragment())
                        .commit();
                break;

            case R.id.nav_about_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUsFragment())
                        .commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startAlbumView() {
        AlbumsFragment albumsFragment = new AlbumsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, albumsFragment)
                .commit();
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
