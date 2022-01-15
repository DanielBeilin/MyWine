package com.example.mywine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.feedFragment);

    }

    FeedFragment feedFragment = new FeedFragment();
    ExploreFragment exploreFragment = new ExploreFragment();
    LikesFragment likesFragment = new LikesFragment();
    ProfileFragment profileFragment = new ProfileFragment();


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feedFragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, feedFragment).commit();
                return true;

            case R.id.exploreFragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, exploreFragment).commit();
                return true;

            case R.id.likesFragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, likesFragment).commit();
                return true;
            case R.id.profileFragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                return true;
        }
        return false;
    }
}