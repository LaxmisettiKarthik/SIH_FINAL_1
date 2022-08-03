package com.elite.sih_final_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.email2);
        resetPasswordButton = findViewById(R.id.reset);

        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }
    private void resetPassword(){
        String email = emailEditText.getText().toString().trim();
        if(email.isEmpty()){
            emailEditText.setError("email is required");
            emailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide valid email!!!");
            emailEditText.requestFocus();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Check your Email to reset your password!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ForgotPassword.this, "Try Again! Something wrong happend!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}