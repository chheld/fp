package de.fischerprofil.fp.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.fischerprofil.fp.R;
import de.fischerprofil.fp.ui.fragments.AboutFragment;
import de.fischerprofil.fp.ui.fragments.WWWFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuItem mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(de.fischerprofil.fp.R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(de.fischerprofil.fp.R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(de.fischerprofil.fp.R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(de.fischerprofil.fp.R.id.drawer_layout);
        setupDrawer();

        NavigationView navigationView = (NavigationView) findViewById(de.fischerprofil.fp.R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        if (savedInstanceState == null) {
            //showFragment("Über ..."); //TODO: restore saved fragment
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(de.fischerprofil.fp.R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_settings:
                showSettings();
                return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                mSelectedItem = menuItem;
                return true;
            }
        });
    }

    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, de.fischerprofil.fp.R.string.drawer_open, de.fischerprofil.fp.R.string.drawer_close) {

            //** Called when a drawer has settled in a completely open state. *//*
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menue");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            //** Called when a drawer has settled in a completely closed state. *//*
            public void onDrawerClosed(View view) {

                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(de.fischerprofil.fp.R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                showFragment(mSelectedItem); //Event erst starten nachdem Drawer geschlossen ist
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    private void showFragment(MenuItem i) {

        int id = i.getItemId();

        Fragment fragment = null;
        Intent intent = null;

        switch (id) {

            case R.id.nav_home: // TODO: Zuletzt verwendete Vorgänge
                break;

            case R.id.nav_www: // TODO: Aufruf der WWW fischerprofil.de
                fragment = new WWWFragment();
                // in default Browser
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("www.fischerprofil.de"));
//                startActivity(browserIntent);
                break;

            case R.id.nav_about:
                fragment = new AboutFragment();
                break;

            case R.id.nav_contacts:
                intent = new Intent(this, ContactListActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_orders:
                intent = new Intent(this, OrderListActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_references:
                intent = new Intent(this, GalleryListActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_vpn:
                showVPN();
                break;

            case R.id.nav_settings_2:
                showSettings();
                break;

            default:
                break;
        }
        if (fragment != null) {
            FragmentManager frgManager = getSupportFragmentManager();
            frgManager.beginTransaction().replace(de.fischerprofil.fp.R.id.fragment_container, fragment).commit();
        }
    }

    private void showVPN() {
        // TODO: VPN automatisch aktivieren statt VPN app zu starten
        PackageManager manager = this.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage("app.openconnect");
        //intent.addCategory(Intent.CATEGORY_LAUNCHER); //TODO: wirklich notwendig ?
        intent.putExtra("Fp", "upb ssl"); // zum direkten Öffenen der FP-Einstellungen, sonst weglassen
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //TODO: wirklich notwendig ?
        this.startActivity(intent);
        //startActivityForResult(intent, 1);
    }

    private void showSettings(){
        Intent intent = new Intent(this, PreferencesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
