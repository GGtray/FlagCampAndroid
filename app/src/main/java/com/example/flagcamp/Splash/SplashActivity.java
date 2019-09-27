package com.example.flagcamp.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flagcamp.Discover.DiscoverActivity;
import com.example.flagcamp.Home.HomeActivity;
import com.example.flagcamp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth
                .getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, DiscoverActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
