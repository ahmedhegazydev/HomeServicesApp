package com.ahmed.homeservices.activites.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.homeservices.activites.login_register.LoginActivity;
import com.ahmed.homeservices.activites.profile.ProfileActivity;
import com.ahmed.homeservices.adapters.MenuAdapter;

import androidx.appcompat.widget.Toolbar;

import com.ahmed.homeservices.fragments.FragmentAboutUs;
import com.ahmed.homeservices.fragments.FragmentBookings;
import com.ahmed.homeservices.fragments.FragmentContactUs;
import com.ahmed.homeservices.fragments.FragmentHelp;
import com.ahmed.homeservices.fragments.FragmentHistory;
import com.ahmed.homeservices.fragments.FragmentNotifications;
import com.ahmed.homeservices.fragments.FragmentSearch;
import com.ahmed.homeservices.fragments.FragmentServices;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.fragments.FragmentSettings;
import com.ahmed.homeservices.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;


public class MainActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {
    private static final String TAG = "MainActivity";
    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    private ArrayList<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));

        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle toolbar actions
        handleToolbar();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        // Show main fragment in container
        goToFragment(new FragmentServices(), false);
        mMenuAdapter.setViewSelected(0, true);
        setTitle(mTitles.get(0));
    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    @Override
    public void onFooterClicked() {
//        Toast.makeText(this, "onFooterClicked", Toast.LENGTH_SHORT).show();

        Utils.getInstance().signOutFromGoogleFacebook(this);

        finish();
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    @Override
    public void onHeaderClicked() {
        //Toast.makeText(this, "onHeaderClicked", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.add(R.id.container, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title
        setTitle(mTitles.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);

        Log.e(TAG, "onOptionClicked: " + position);

        Fragment fragment = null;

        // Navigate to the right fragment
        switch (position) {
            case 0:
                fragment = new FragmentServices();
                break;
            case 1:
                fragment = new FragmentBookings();
                break;
            case 2:
                fragment = new FragmentSearch();
                break;
            case 3:
                fragment = new FragmentHistory();
                break;
            case 4:
                fragment = new FragmentSettings();
                break;
            case 5:
                fragment = new FragmentNotifications();
                break;
            case 6:
                fragment = new FragmentHelp();
                break;
            case 7:
                fragment = new FragmentAboutUs();
                break;
            case 8:
                fragment = new FragmentContactUs();
                break;
            default:

                break;

        }

        goToFragment(fragment, false);


        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }


}
