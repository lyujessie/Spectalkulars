package com.example.jiexinlyu.spectalkulars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.HashSet;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void emailSignup(View view) {
        Intent intent = new Intent(LoginActivity.this, EmailSignupActivity.class);
        startActivity(intent);
    }

    public void signin(View view) {
        Intent intent = new Intent(LoginActivity.this, SigninActivity.class);
        startActivity(intent);
    }
}
