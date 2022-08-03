package com.elite.sih_final_1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//facebook multi user login
//import com.facebook.AccessToken;
//import com.facebook.AccessTokenTracker;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.appevents.AppEventsLogger;


//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signin, register;
    private TextView forgotPassword;
    private EditText emailText, passwordText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signin = findViewById(R.id.signIn);
        register = findViewById(R.id.register);
        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(this);
        signin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.signIn:
                userLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }

    }

    private void userLogin() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (email.isEmpty()) {
            emailText.setError("Email is required!!");
            emailText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Please provide valid email!");
            emailText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordText.setError("password is required!!");
            passwordText.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordText.setError("min password length is 6 characters");
            passwordText.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (task.isSuccessful()) {
                    if (user.isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check Your Email to verify your account!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
