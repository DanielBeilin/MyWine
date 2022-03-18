package com.example.mywine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.mywine.model.UserModelStorageFunctions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ActionBar actionBar;
    String userId;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2, new FeedFragment()).commit();

    }

    private void init() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Feed");

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        userId = UserModelStorageFunctions.instance.getLoggedInUser().getUid();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.feedFragment:
                        selectedFragment = new FeedFragment();
                        actionBar.setTitle("Feed");
                        break;
                    case R.id.exploreFragment:
                        selectedFragment = new ExploreFragment();
                        actionBar.setTitle("Explore");
                        break;
                    case R.id.addPostFragment:
                        selectedFragment = new AddPostFragment();
                        actionBar.setTitle("Add Post");
                        break;
                    case R.id.profileFragment:
                        selectedFragment = new ProfileFragment();
                        actionBar.setTitle("Profile");
                        break;
                    default:
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2, selectedFragment).commit();
                return true;
                }
            };
}