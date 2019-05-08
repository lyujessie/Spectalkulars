package com.example.jiexinlyu.spectalkulars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.HashSet;

public class SigninActivity extends AppCompatActivity {

    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        username = (EditText) findViewById(R.id.signin_username_entry);
    }

    public void signin_redirect(View view) {
        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username.getText().toString());
        startActivity(intent);
    }
}
