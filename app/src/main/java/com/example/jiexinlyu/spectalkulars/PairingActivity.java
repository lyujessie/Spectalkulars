package com.example.jiexinlyu.spectalkulars;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.type.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PairingActivity extends AppCompatActivity {
    private static final String TAG = PairingActivity.class.getSimpleName();

    private String username;
    private FirebaseFirestore db;
    private HashMap<String, Object> matchedUsers;
    private ArrayList<String> userInterests;
    private TextView matchUserName;
    private LinearLayout topics;
    private String matchUser;
    private String refusedUser;
    private ArrayList<String> commonInterestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        username = getIntent().getStringExtra("USERNAME");
        refusedUser = getIntent().getStringExtra("REFUSEDUSER");
        matchedUsers = new HashMap<>();
        matchUserName = (TextView) findViewById(R.id.matchuser_name);
        topics = (LinearLayout) findViewById(R.id.topic_list);
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                matchedUsers.put(document.getId(), document.getData());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            HashMap<String, Object> userData = (HashMap<String, Object>) matchedUsers.get(username);
                            userInterests = (ArrayList<String>) userData.get("interests");
                            matchedUsers.remove(username);

                            if (refusedUser != null) {
                                matchedUsers.remove(refusedUser);
                            }

                            for (Map.Entry<String,Object> user_entry : matchedUsers.entrySet()) {
                                String match_username = user_entry.getKey();
                                HashMap<String, Object> match_userData = (HashMap<String, Object>) user_entry.getValue();
                                ArrayList<String> match_userInterests = (ArrayList<String>) match_userData.get("interests");

                                ArrayList<String> commonInterests = new ArrayList<>();
                                for (String interest : match_userInterests) {
                                    if (userInterests.contains(interest)) {
                                        commonInterests.add(interest);
                                    }
                                }
                                matchedUsers.put(match_username, commonInterests);
                            }
                            setText();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setText() {
        for (Map.Entry<String, Object> user_entry : matchedUsers.entrySet()) {
            matchUser = user_entry.getKey();
            commonInterestsList = (ArrayList<String>) user_entry.getValue();
            topics.removeAllViews();
            for (final String interest : commonInterestsList) {
                final Button topic_bt = new Button(this);
                topic_bt.setText(interest);
                topic_bt.setBackgroundResource(R.drawable.tag_topic);
                topic_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> newUserData = new HashMap<String, Object>() {{
                            put("topic", interest);
                            put("match_user", matchUser);
                            put("match", true);
                        }};

                        HashMap<String, Object> newMatchUserData = new HashMap<String, Object>() {{
                            put("topic", interest);
                            put("match_user", username);
                            put("match", true);
                        }};

                        db.collection("users").document(username)
                                .set(newUserData, SetOptions.merge());

                        db.collection("users").document(matchUser)
                                .set(newMatchUserData, SetOptions.merge());

                        Intent intent = new Intent(PairingActivity.this, WaitActivity.class);
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("MATCHUSER", matchUser);
                        startActivity(intent);
                    }
                });

                topics.addView(topic_bt);
            }
            matchUserName.setText(matchUser);
            break;
        }
    }

    public void redoButton(View view) {
        matchedUsers.remove(matchUser);
        setText();
    }

    public void cancelPairing(View view) {
        Intent intent = new Intent(PairingActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
