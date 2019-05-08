package com.example.jiexinlyu.spectalkulars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EmailSignupActivity extends AppCompatActivity {

    private EditText usernameEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_signup);

        usernameEntry = (EditText) findViewById(R.id.username_entry);
    }

    public void infoSignup(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String username = usernameEntry.getText().toString();
        HashMap<String, Object> newUserData = new HashMap<String, Object>() {{
            put("match", false);
            put("match_user", "");
            put("status", "open");
            put("topic", "");
            put("user_info", "");
            put("interests", "");
            put("recent", "");
        }};
        db.collection("users").document(username)
                .set(newUserData);

        Intent intent = new Intent(EmailSignupActivity.this, InfoSignupActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
