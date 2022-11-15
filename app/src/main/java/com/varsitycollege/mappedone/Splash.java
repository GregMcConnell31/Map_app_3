package com.varsitycollege.mappedone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int display_length=5000;
        new Handler().postDelayed(()->{
            Intent intro= new Intent(Splash.this, sign_in.class);
            startActivity(intro);
            this.finish();

        },display_length);
    }
}