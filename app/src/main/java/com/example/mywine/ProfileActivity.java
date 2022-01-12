package com.example.mywine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    Button logoutBtn;
    TextView userName, userEmail;
    ImageView profileImage;
    private FirebaseAuth mfirebaseAuth;
    private final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        Log.d(TAG, "onCreate");
        mfirebaseAuth = FirebaseAuth.getInstance();
        validateUser();

        logoutBtn=(Button)findViewById(R.id.logoutBtn);
        userName=(TextView)findViewById(R.id.name);
        userEmail=(TextView)findViewById(R.id.email);
        profileImage=(ImageView)findViewById(R.id.profileImage);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfirebaseAuth.signOut();
                validateUser();
            }
        });
    }

    private void validateUser() {
        FirebaseUser firebaseUser = mfirebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            //user is not logged in
            startActivity(new Intent(ProfileActivity.this, SignInActivity.class));
            finish();
        } else {
            // user logged in
            userName.setText(firebaseUser.getDisplayName());
            userEmail.setText(firebaseUser.getEmail());
            profileImage.setImageURI(firebaseUser.getPhotoUrl());
        }
    }
}
