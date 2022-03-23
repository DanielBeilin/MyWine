package com.example.mywine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mywine.model.User.User;
import com.example.mywine.model.UserModelStorageFunctions;
import com.example.mywine.utils.InputValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    MaterialButton RegisterBtn;
    TextView LoginLink;
    TextInputLayout EmailInputLayout, PasswordInputLayout, NameInputLayout;
    TextInputEditText EmailEditText, PasswordEditText, NameEditText;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        init();
        setListeners();
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        LoginLink = findViewById(R.id.login);
        RegisterBtn = findViewById(R.id.registerButton);
        EmailInputLayout = findViewById(R.id.emailInputLayout);
        PasswordInputLayout = findViewById(R.id.passwordInputLayout);
        NameInputLayout = findViewById(R.id.nameInputLayout);
        EmailEditText = findViewById(R.id.emailEditText);
        PasswordEditText = findViewById(R.id.passwordEditText);
        NameEditText = findViewById(R.id.nameEditText);
    }

    private void setListeners() {
        onLoginLinkClickListener();
        onRegisterButtonClick();
    }

    private void onLoginLinkClickListener() {
        LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onRegisterButtonClick() {
        RegisterBtn.setOnClickListener(view -> {
            setErrorIfFullNameIsInvalid();
            setErrorIfEmailIsInvalid();
            setErrorIfPasswordIsInvalid();
            if(isFormValid()) {
                registerUser(NameEditText.getText().toString(),
                        PasswordEditText.getText().toString(),
                        NameEditText.getText().toString());
            }
        });
    }

    private void registerUser(String email, String password, String name) {
        RegisterBtn.setEnabled(false);
        progressDialog.show();
        User user = new User(name,email);
        UserModelStorageFunctions.instance.addUser(user, password,()->{
            Toast.makeText(RegisterActivity.this, " Registered Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
            finish();
        });
    }

    private void setErrorIfFullNameIsInvalid() {
        if (!InputValidator.isFullNameValid(NameEditText.getText())) {
            NameInputLayout.setError(getString(R.string.required));
        } else {
            NameInputLayout.setError(null);
        }
    }

    private void setErrorIfEmailIsInvalid() {
        if (!InputValidator.isEmailValid(EmailEditText.getText())) {
            EmailEditText.setError(getString(R.string.email_invalid));
        } else {
            EmailInputLayout.setError(null);
        }
    }

    private void setErrorIfPasswordIsInvalid() {
        if (!InputValidator.isPasswordValid(PasswordEditText.getText())) {
            PasswordInputLayout.setError(getString(R.string.password_invalid));
        } else {
            PasswordInputLayout.setError(null);
        }
    }

    private boolean isFormValid() {
        return (InputValidator.isFullNameValid(NameEditText.getText()) &&
                InputValidator.isEmailValid(EmailEditText.getText()) &&
                InputValidator.isPasswordValid(PasswordEditText.getText()));
    }
}
