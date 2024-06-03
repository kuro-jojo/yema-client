package com.kuro.yema.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.kuro.yema.R;

public class LoadingScreenActivity extends AppCompatActivity {
    private static final int DELAY = 1000;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mFirebaseAuth.getCurrentUser() != null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            finish();
        }, DELAY);

    }
}