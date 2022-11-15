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

public class sign_up extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button signUpButton;
    private TextView signInButton;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailField=findViewById(R.id.emailEt);
        passwordField=findViewById(R.id.passET);
        confirmPasswordField=findViewById(R.id.confirmPassEt);
        signUpButton=findViewById(R.id.signInButton);
        signInButton=findViewById(R.id.signUpOption);
        fAuth=FirebaseAuth.getInstance();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(sign_up.this, sign_in.class);
                startActivity(i);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String confirmPassword=confirmPasswordField.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    emailField.setError("email field empty");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordField.setError("password field empty");
                    return;
                }
                if(!password.equals(confirmPassword)){
                    confirmPasswordField.setError("passwords must match");
                    return;
                }

                Task<AuthResult> authResultTask = fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(sign_up.this, "account created successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(sign_up.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(sign_up.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });
    }
}