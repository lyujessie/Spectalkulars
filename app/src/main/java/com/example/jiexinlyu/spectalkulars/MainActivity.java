package com.example.jiexinlyu.spectalkulars;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;

import com.google.firebase.firestore.SetOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String username;
    private TextView usernameView;

    // Views
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("USERNAME");
        usernameView = (TextView) findViewById(R.id.username_main);
        usernameView.setText(username);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(username)
                .addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            if (snapshot.getData().get("match").equals(true)) {
                                ArrayList<String> topic = (ArrayList<String>) snapshot.getData().get("topic");
                                String match_user = snapshot.getData().get("match_user").toString();
                                notifyMatch(topic, match_user);
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    private void updateDB(String collection, String document, Map<String, Object> data) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update interest topics
        db.collection(collection).document(document)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void startPairing(View view) {
        Intent intent = new Intent(MainActivity.this, PairingActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    public void notifyMatch(final ArrayList<String> topic, final String match_user) {
        final SlidingUpPanelLayout mainActivityLayout = (SlidingUpPanelLayout) findViewById(R.id.main_activity_layout);

        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View notificationView = layoutInflater.inflate(R.layout.match_notification,null);

        Button closeNotificationButton = (Button) notificationView.findViewById(R.id.close_notification_bt);
        Button startTalkingButton = (Button) notificationView.findViewById(R.id.start_talking_bt);

        //instantiate popup window
        popupWindow = new PopupWindow(notificationView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

        //display the popup window
        TextView matchTopic = (TextView) popupWindow.getContentView().findViewById(R.id.match_topic);
        TextView matchUseraname = (TextView) popupWindow.getContentView().findViewById(R.id.match_userid);
        StringBuilder topicTxt = new StringBuilder();
        for (String singleTopic: topic) {
            topicTxt.append(singleTopic + "\n");
        }
        matchTopic.setText(topicTxt);
        matchUseraname.setText(match_user);
        popupWindow.showAtLocation(mainActivityLayout, Gravity.CENTER, 0, 0);

        //prepare for talking on button click
        startTalkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "talking");

                updateDB("users", match_user, data);
                updateDB("users", username, data);

                // Transmit topic info
                Intent intent = new Intent(MainActivity.this, TalkingActivity.class);
                intent.putExtra("TOPIC", topic);
                intent.putExtra("USERNAME", username);
                intent.putExtra("MATCHUSER", match_user);
                startActivity(intent);
            }
        });

        //close the popup window on button click
        closeNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                Map<String, Object> data = new HashMap<>();
                data.put("match", false);
                data.put("status", "open");
                data.put("topic", "");
                data.put("match_user", "");

                updateDB("users", match_user, data);
                updateDB("users", username, data);
            }
        });
    }

    public void gotoSetting (View view) {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    public void gotoStore (View view) {
        Intent intent = new Intent(MainActivity.this, StoreActivity.class);
        startActivity(intent);
    }
}
