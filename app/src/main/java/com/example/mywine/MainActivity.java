package com.example.mywine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Feed");

        firebaseAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2, new FeedFragment()).commit();


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

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2, selectedFragment).commit();
                return true;
                }
            };

    private void checkUSerStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            // TODO: user is already signed in
        } else {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
    }
}