package com.fiek.hitchhikerkosova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.fiek.hitchhikerkosova.ui.AddPostFragment;
import com.fiek.hitchhikerkosova.ui.MainPostsFragment;
import com.fiek.hitchhikerkosova.ui.ReservedRidesFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    Fragment fragment = null;
    Class fragmentClass;
    FragmentManager fragmentManager;
    private FirebaseAuth mAuth;
    TextView tvLogout,tvHeaderName,tvHeaderEmail;
    FirebaseUser currentUser;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Listeneri per me ndegju kur firebaseAuth e ndrron gjendjen(behet logout)
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                if (mAuth.getCurrentUser() == null){
                    startActivity(new Intent(MainActivity.this, LogInActivity.class));
                    finish();
                }
                else {
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(authStateListener);

        //E nderrojm ActionBar me ToolBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        //Ne create loadohet fragmenti 1
        fragmentClass = MainPostsFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fContent, fragment).commit();

        //Listeneri per me u qel hamburger menu
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer  , toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_hamburger);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Toolbar title");

        tvLogout=findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               tvLogOutFunc();
            }
        });

        View headerLayout=nvDrawer.getHeaderView(0);

        tvHeaderName=(TextView) headerLayout.findViewById(R.id.tvHeaderName);
        tvHeaderEmail=(TextView) headerLayout.findViewById(R.id.tvHeaderEmail);

        currentUser = mAuth.getCurrentUser();

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser ==null){
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
            finish();
        }
        tvHeaderName.setText(currentUser.getDisplayName());
        tvHeaderEmail.setText(currentUser.getEmail());
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Navigimi neper fragmente

        switch(menuItem.getItemId()) {
            case R.id.nav_rides_fragment:
                fragmentClass = MainPostsFragment.class;
                break;
            case R.id.nav_reserved_fragment:
                fragmentClass = ReservedRidesFragment.class;
                break;

            default:
                fragmentClass = MainPostsFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fContent, fragment).commit();


        menuItem.setChecked(true);
         setTitle(menuItem.getTitle());
        // Mbyllja e menus
        mDrawer.closeDrawers();
    }

    // Funksioni i butonit per te shtuar Postime
    public void btnNewPostFunc(View v){
        fragmentClass = AddPostFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fContent, fragment).commit();
        for (int i = 0; i < nvDrawer.getMenu().size(); i++) {
            nvDrawer.getMenu().getItem(i).setChecked(false);
        }
    }
    //Funksioni i LogOut butonit
    private void tvLogOutFunc() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage(R.string.alertLogOut)
                .setPositiveButton(R.string.tvLogOut, new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {

                        mAuth.signOut();

                    }
                }).setNegativeButton(R.string.btnCancel, null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }


}
