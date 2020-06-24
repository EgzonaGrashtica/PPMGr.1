package com.fiek.hitchhikerkosova.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavController.OnDestinationChangedListener;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.fragments.EditProfileFragment;
import com.fiek.hitchhikerkosova.utils.UploadProfilePicture;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements OnDestinationChangedListener {

    private final int TAKE_IMAGE_CODE=10001;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    NavController navController;
    private FirebaseAuth mAuth;
    TextView tvLogout,tvHeaderName,tvHeaderEmail;
    ShapeableImageView profilePicImageView;
    FirebaseUser currentUser;
    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;
    ImageButton btnNewPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNewPost=(ImageButton) findViewById(R.id.btnNewPost);

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


        //Listeneri per me u qel hamburger menu
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer  , toolbar, R.string.drawer_open, R.string.drawer_close);
        View headerLayout=nvDrawer.getHeaderView(0);
        tvHeaderName=(TextView) headerLayout.findViewById(R.id.tvHeaderName);
        tvHeaderEmail=(TextView) headerLayout.findViewById(R.id.tvHeaderEmail);
        profilePicImageView=(ShapeableImageView) headerLayout.findViewById(R.id.profilePicImageView);
        currentUser = mAuth.getCurrentUser();
        navController=Navigation.findNavController(MainActivity.this,R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(this);
        tvHeaderName.setText(currentUser.getDisplayName());
        tvHeaderEmail.setText(currentUser.getEmail());
        if(currentUser.getPhotoUrl() !=null){
            Glide.with(MainActivity.this).load(currentUser.getPhotoUrl()).into(profilePicImageView);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser ==null){
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
            finish();
        }

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
                break;
            case R.id.nav_reserved_fragment:
                navController.navigate(R.id.reservedRidesFragment);
                toolbar.setTitle(getResources().getString(R.string.ReservedRidesTitle));
                break;
            case R.id.nav_myPostedRides_fragment:
                    navController.navigate(R.id.myPostedRides);
                toolbar.setTitle(getResources().getString(R.string.MyPostedRidesTitle));
                break;
            case R.id.nav_profile:
                navController.navigate(R.id.editProfileFragment);
                toolbar.setTitle(R.string.EditProfileFragment);
                break;
            case R.id.nav_logout:
                tvLogOutFunc();
                break;
        }

        if(menuItem.getItemId() !=R.id.nav_logout){
            // Mbyllja e menus
            mDrawer.closeDrawers();
        }

    }

    // Funksioni i butonit per te shtuar Postime
    public void btnNewPostFunc(View v){
        navController.navigate(R.id.action_mainPostsFragment_to_addPostFragment);
        toolbar.setTitle(getResources().getString(R.string.AddPostTitle));
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

    private void setUpToolbar() {

        if (navController.getCurrentDestination().getLabel().equals("fragment_main_posts")) {
            btnNewPost.setVisibility(View.VISIBLE);
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            });
            drawerToggle.setHomeAsUpIndicator(R.drawable.ic_hamburger);
            for (int i = 0; i < nvDrawer.getMenu().size(); i++) {
                nvDrawer.getMenu().getItem(i).setChecked(false);
            }

            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("");
            tvHeaderName.setText(currentUser.getDisplayName());
            tvHeaderEmail.setText(currentUser.getEmail());
            if(currentUser.getPhotoUrl() !=null){
                Glide.with(MainActivity.this).load(currentUser.getPhotoUrl()).into(profilePicImageView);
            }else{
                profilePicImageView.setImageResource(R.drawable.default_profile_pic);
            }
        } else {
            btnNewPost.setVisibility(View.GONE);
            drawerToggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18pt_2x);
            drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }


    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        setUpToolbar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
