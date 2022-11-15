package com.varsitycollege.mappedone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class sign_in extends AppCompatActivity {
    private TextView signUp;
    private EditText emailField;
    private EditText passwordField;
    private Button btnSignIn;
    FirebaseAuth FAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signUp=findViewById(R.id.signUpOption);
        emailField=findViewById(R.id.emailEt);
        passwordField=findViewById(R.id.passET);
        FAuth=FirebaseAuth.getInstance();
        btnSignIn=findViewById(R.id.signInButton);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    emailField.setError("email field cannot be left empty");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordField.setError("Password field cannot be left empty");
                    return;
                }
                FAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(sign_in.this, "User has been logged in successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(sign_in.this, MainActivity.class);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(sign_in.this, "Error signing in", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(sign_in.this, sign_up.class);
                startActivity(i);
            }
        });

    }
}