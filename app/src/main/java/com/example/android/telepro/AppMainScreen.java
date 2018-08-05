package com.example.android.telepro;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.telepro.fragments.FullScreenModeSettingsFragment;
import com.example.android.telepro.fragments.MainScreenFragment;

public class AppMainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MainScreenFragment mainScreenFragment;
    FullScreenModeSettingsFragment fullScreenModeSettingsFragment;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ArticleScreen.class);
                startActivity(intent);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.main_screen);
        navigationView.setNavigationItemSelectedListener(this);

        fullScreenModeSettingsFragment = new FullScreenModeSettingsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mainScreenFragment = new MainScreenFragment();
        if (savedInstanceState==null) {
            fragmentTransaction.add(R.id.main_container, mainScreenFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        int id = item.getItemId();

        if (id == R.id.nav_fullscreen_settings) {
            fragmentTransaction.replace(R.id.main_container, fullScreenModeSettingsFragment);
            fragmentTransaction.commit();
            if (fab.getVisibility() == View.VISIBLE) {
                fab.setVisibility(View.GONE);
            }

        } else if (id == R.id.main_screen) {
            fragmentTransaction.replace(R.id.main_container, mainScreenFragment);
            fragmentTransaction.commit();
            if (fab.getVisibility() == View.GONE) {
                fab.setVisibility(View.VISIBLE);
            }
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}