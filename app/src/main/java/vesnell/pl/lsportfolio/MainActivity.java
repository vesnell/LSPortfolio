package vesnell.pl.lsportfolio;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private TextView toolbarTitle;
    private LinearLayout ll;
    private FrameLayout fragContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitle.setText(getString(R.string.menu_apps));

        fragContainer = (FrameLayout) findViewById(R.id.flFragmentContainer);
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setId(R.id.fragment_container_id);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, null);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(drawable);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.apps) {
            setViewApps();
        } else if (id == R.id.contact) {
            setViewContact();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setViewApps() {
        toolbarTitle.setText(getString(R.string.menu_apps));

        removeFragment(AppsFragment.TAG);
        removeFragment(ContactFragment.TAG);
        getSupportFragmentManager().beginTransaction().add(ll.getId(), AppsFragment.newInstance(), AppsFragment.TAG).commit();
        fragContainer.addView(ll);
    }

    private void setViewContact() {
        toolbarTitle.setText(getString(R.string.menu_contact));

        removeFragment(AppsFragment.TAG);
        removeFragment(ContactFragment.TAG);
        getSupportFragmentManager().beginTransaction().add(ll.getId(), ContactFragment.newInstance(), ContactFragment.TAG).commit();
        fragContainer.addView(ll);
    }

    private void removeFragment(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            fragContainer.removeAllViews();
        }
    }
}
