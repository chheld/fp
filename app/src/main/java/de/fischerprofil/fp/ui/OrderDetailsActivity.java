package de.fischerprofil.fp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.fischerprofil.fp.AppController;
import de.fischerprofil.fp.R;
import de.fischerprofil.fp.adapter.OrderTabPagerAdapter;
import de.fischerprofil.fp.ui.fragments.AboutFragment;
import de.fischerprofil.fp.ui.fragments.HintFragment;
import de.fischerprofil.fp.ui.fragments.OrderDetailsFragment;

public class OrderDetailsActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private AppController mAppController;
    private final String VOLLEY_TAG = "VOLLEY_TAG_OrderDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetails);

        mAppController = AppController.getInstance();

        setupToolbar();

        setupTabLayout();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final OrderTabPagerAdapter adapter = new OrderTabPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

        Bundle bundle = new Bundle();
        bundle.putString("anr", getIntent().getStringExtra("anr"));
        adapter.setArguments(bundle);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(de.fischerprofil.fp.R.menu.menu_activity_orderdetails, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStop() {
        super.onStop();
        // This will tell to Volley to cancel all the pending requests
        mAppController.cancelPendingRequests(VOLLEY_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Menüauswahl verarbeiten
        int id = item.getItemId();

        switch (id) {

            case de.fischerprofil.fp.R.id.action_settings:
                showSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettings(){
        Intent intent = new Intent(this, PreferencesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showFragment(String key, @Nullable Bundle args) {

        Fragment fragment = null;
        switch (key) {

            case "hint":
                fragment = new HintFragment();
                break;

            case "about":
                fragment = new AboutFragment();
                break;

            case "details":
                fragment = new OrderDetailsFragment();
                fragment.setArguments(args);
                break;

            default:
                break;
        }
        if (fragment != null) {
            FragmentManager frgManager = getSupportFragmentManager();
            frgManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    private void setupToolbar(){

        Intent intent = getIntent();
        String anr = intent.getStringExtra("anr");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.activity_orderdetails_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Auftrag " + anr);
            //getSupportActionBar().setSubtitle(a);
        }
    }

    private void setupTabLayout(){

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        if(mTabLayout == null) return;

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addTab(mTabLayout.newTab().setText("Übersicht"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Details"));
    }

}
