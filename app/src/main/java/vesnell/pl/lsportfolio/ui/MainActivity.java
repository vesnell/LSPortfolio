package vesnell.pl.lsportfolio.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import vesnell.pl.lsportfolio.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String FRAG_TYPE = "fragType";

    private DrawerLayout drawer;
    private TextView toolbarTitle;
    private LinearLayout ll;
    private FrameLayout fragContainer;
    private AppsFragment appsFragment;
    private ContactFragment contactFragment;
    private UIInterface fragType = UIInterface.APPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

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

        setViewApps(savedInstanceState);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.apps) {
            fragType = UIInterface.APPS;
            setViewApps();
        } else if (id == R.id.contact) {
            fragType = UIInterface.DETAILS;
            setViewContact();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setViewApps(Bundle savedInstanceState) {
        if (ll != null) {
            if(savedInstanceState == null) {
                setViewApps();
            } else {
                UIInterface fragType = (UIInterface) savedInstanceState.getSerializable(FRAG_TYPE);
                switch (fragType) {
                    case APPS:
                        if (appsFragment != null) {
                            setViewApps();
                        } else {
                            appsFragment = (AppsFragment) getSupportFragmentManager().findFragmentByTag(AppsFragment.TAG);
                        }
                        break;
                    case DETAILS:
                        if (contactFragment != null) {
                            setViewContact();
                        } else {
                            contactFragment = (ContactFragment) getSupportFragmentManager().findFragmentByTag(ContactFragment.TAG);
                        }
                        break;
                }
            }
        }
    }

    private void setViewApps() {
        toolbarTitle.setText(getString(R.string.menu_apps));

        appsFragment = new AppsFragment();
        getSupportFragmentManager().beginTransaction().replace(fragContainer.getId(), appsFragment, AppsFragment.TAG).commit();
    }

    private void setViewContact() {
        toolbarTitle.setText(getString(R.string.menu_contact));

        contactFragment = new ContactFragment();
        getSupportFragmentManager().beginTransaction().replace(fragContainer.getId(), contactFragment, ContactFragment.TAG).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(FRAG_TYPE, fragType);
    }
}
