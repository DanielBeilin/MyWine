package com.example.mywine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywine.model.UserModelStorageFunctions;
import com.example.mywine.utils.InputValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    MaterialButton loginButton;
    TextView registerLink;
    TextInputLayout emailInputLayout, passwordInputLayout, nameInputLayout;
    TextInputEditText emailEditText, passwordEditText, nameEditText;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        init();
        setListeners();
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loggin In...");

        registerLink = findViewById(R.id.lnkRegister);
        loginButton = findViewById(R.id.loginButton);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);

    }

    private void setListeners() {
        onRegisterLinkClick();
        onLoginButtonClicked();
        onPasswordEditTextListener();
        onEmailEditTextListener();
    }

    private void onRegisterLinkClick() {
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onLoginButtonClicked() {
        loginButton.setOnClickListener(view -> {
            setErrorIfEmailIsInvalid();
            setErrorIfPasswordIsInvalid();
            if(isFormValid()) {
                loginButton.setEnabled(false);
                registerLink.setEnabled(false);
                progressDialog.show();
                signInUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    private boolean isFormValid() {
        return (setErrorIfEmailIsInvalid() &&
                setErrorIfPasswordIsInvalid());
    }

    private boolean setErrorIfEmailIsInvalid() {
        if (!InputValidator.isEmailValid(emailEditText.getText())) {
            emailInputLayout.setError(getString(R.string.email_invalid));
            return false;
        } else {
            emailInputLayout.setError(null);
            return true;
        }
    }

    private boolean setErrorIfPasswordIsInvalid() {
        if (!InputValidator.isPasswordValid(passwordEditText.getText())) {
            passwordInputLayout.setError(getString(R.string.password_invalid));
            return false;
        } else {
            passwordInputLayout.setError(null);
            return true;
        }
    }

    private void onEmailEditTextListener() {
        emailEditText.setOnKeyListener((view, i, keyEvent) -> {
            setErrorIfEmailIsInvalid();
            return false;
        });
    }

    private void onPasswordEditTextListener() {
        passwordEditText.setOnKeyListener((view, i, keyEvent) -> {
            setErrorIfPasswordIsInvalid();
            return false;
        });
    }

    private void signInUser(String email, String password) {
        UserModelStorageFunctions.instance.signIn(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                () -> {
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                },
                errorMessage -> {
                    Toast.makeText(SignInActivity.this, "error occurred trying to sign in!", Toast.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                    registerLink.setEnabled(true);
                    progressDialog.hide();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}