package com.example.mywine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywine.model.User.User;
import com.example.mywine.model.UserModelStorageFunctions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    Button RegisterBtn;
    TextView LoginLink;
    EditText EmailInput, PasswordInput, NameInput;
    ProgressDialog progressDialog;
    //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Register");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        LoginLink = findViewById(R.id.lnkLogin);
        RegisterBtn = findViewById(R.id.btnRegister);
        EmailInput = findViewById(R.id.txtEmail);
        PasswordInput = findViewById(R.id.txtPwd);
        NameInput = findViewById(R.id.txtName);

        LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailInput.getText().toString().trim();
                String password = PasswordInput.getText().toString().trim();
                String name = NameInput.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    EmailInput.setError("Invalid Email!");
                    EmailInput.setFocusable(true);
                } else if (password.length() < 6) {
                    PasswordInput.setError("Password length has to be at least 6 characters!");
                    PasswordInput.setFocusable(true);
                } else {
                    registerUser(email, password,name);
                }
            }
        });
    }

    private void registerUser(String email, String password,String name) {
        progressDialog.show();
        User newuser= new User(name,email);
        UserModelStorageFunctions.instance.addUser(newuser,password,()->{
            Toast.makeText(RegisterActivity.this, " Registered Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
            finish();
        });
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            String email = user.getEmail();
//                            String uid = user.getUid();
//                            String name ="";
//                            String img = "";
//                            HashMap<Object, String> hashMap = new HashMap<>();
//                            // TODO: get values from edit profile
//                            hashMap.put("email", email);
//                            hashMap.put("name", name);
//                            hashMap.put("uid", uid);
//                            hashMap.put("image", img);
//                            FirebaseDatabase db = FirebaseDatabase.getInstance();
//                            DatabaseReference ref = db.getReference("Users");
//                            ref.child(uid).setValue(hashMap);
//
//                            Toast.makeText(RegisterActivity.this, " Registered Successfully!", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
//                            finish();
//                        } else {
//                            progressDialog.dismiss();
//                            Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//       });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
