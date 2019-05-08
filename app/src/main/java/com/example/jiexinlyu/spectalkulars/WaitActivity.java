package com.example.jiexinlyu.spectalkulars;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class WaitActivity extends AppCompatActivity {

    private static final String TAG = PairingActivity.class.getSimpleName();
    private String username;
    private String matchUser;
    private String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        username = getIntent().getStringExtra("USERNAME");
        matchUser = getIntent().getStringExtra("MATCHUSER");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(username)
                .addSnapshotListener(WaitActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            if (snapshot.getData().get("match").equals(false)) {
                                clearMatch();
                            } else if (snapshot.getData().get("status").equals("talking")) {
                                matchUser = snapshot.getData().get("match_user").toString();
                                topic = (String) snapshot.getData().get("topic");
                                startTalking();
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    private void clearMatch () {
        Intent intent = new Intent(WaitActivity.this, PairingActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("REFUSEDUSER", matchUser);
        startActivity(intent);
    }

    private void startTalking() {
        Intent intent = new Intent(WaitActivity.this, TalkingActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("MATCHUSER", matchUser);
        intent.putExtra("TOPIC", topic);
        startActivity(intent);
    }

    public void cancelWait(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> newUserData = new HashMap<String, Object>() {{
            put("topic", "");
            put("match_user", "");
            put("match", false);
        }};

        db.collection("users").document(username)
                .set(newUserData, SetOptions.merge());

        db.collection("users").document(matchUser)
                .set(newUserData, SetOptions.merge());

        Intent intent = new Intent(WaitActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
