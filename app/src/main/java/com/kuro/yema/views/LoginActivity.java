package com.kuro.yema.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.kuro.yema.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;

    public LoginActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}