package com.example.jiexinlyu.spectalkulars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InterestSignupActivity extends AppCompatActivity {

    private ArrayList<String> topics;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_signup);

        username = getIntent().getStringExtra("USERNAME");
        topics = new ArrayList<>();

    }

    public void onClick (View view) {
        Button button = (Button) view;
        String tag = button.getText().toString();
        if (!topics.contains(tag)) {
            topics.add(tag);
            view.setBackgroundResource(R.drawable.tag_selected);
        } else {
            topics.remove(tag);
            view.setBackgroundResource(R.drawable.tag);
        }
    }

    public void gotoHomepage (View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> newUserData = new HashMap<String, Object>() {{
            put("interests", topics);
        }};

        db.collection("users").document(username)
                .set(newUserData, SetOptions.merge());

        Intent intent = new Intent(InterestSignupActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
