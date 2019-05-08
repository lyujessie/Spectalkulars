package com.example.jiexinlyu.spectalkulars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.HashSet;

public class InfoSignupActivity extends AppCompatActivity {

    private TextView usernameView;
    private EditText hometownText;
    private EditText schoolText;

    private HashMap<String, String> userInfo;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_signup);

        username = getIntent().getStringExtra("USERNAME");

        usernameView = (TextView) findViewById(R.id.user_name);
        hometownText = (EditText) findViewById(R.id.hometown_entry);
        schoolText = (EditText) findViewById(R.id.school_entry);

        userInfo = new HashMap<>();

        usernameView.setText(username);
    }

    public void interestSignup(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        userInfo.put("hometown", hometownText.getText().toString());
        userInfo.put("school", schoolText.getText().toString());

        HashMap<String, Object> newUserData = new HashMap<String, Object>() {{
            put("user_info", userInfo);
        }};

        db.collection("users").document(username)
                .set(newUserData, SetOptions.merge());

        Intent intent = new Intent(InfoSignupActivity.this, InterestSignupActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
