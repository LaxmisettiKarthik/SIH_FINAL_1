package com.elite.sih_final_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


//import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView banner;
    private EditText editTextEmail, editTextPassword,confirmpassword;
    private Button registerUser;
   // private LottieAnimationView lottie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


      //  lottie = findViewById(R.id.registerAnimation);

        mAuth = FirebaseAuth.getInstance(); 


        banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);
        registerUser = findViewById(R.id.register1);
        registerUser.setOnClickListener(this);

        editTextEmail = findViewById(R.id.email1);
        editTextPassword = findViewById(R.id.password1);
        confirmpassword = findViewById(R.id.ConfirmPassword);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.register1:
                registerUser();
                break;

        }
    }

    private void registerUser () {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cpassword = confirmpassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter valid email");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("password is required");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Minimum password length is 6 characters!");
            editTextPassword.requestFocus();
            return;
        }
        if(!password.equals(cpassword)){
            confirmpassword.setError("Password hasn't matched");
            confirmpassword.requestFocus();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {  // if user is created, task is successful.
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) { // we'll toast a message that user is registered successfully.
                            Toast.makeText(RegisterActivity.this, "User Registered Successfully!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            // anything wrong happens, we'll toast a message that user is failed to register.
                            Toast.makeText(RegisterActivity.this, "Failed to Register!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }
}