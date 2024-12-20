package com.example.mytinderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Cards cards;
    private ArrayAdapter arrayAdapter;
    private int i;
    private String userSex;
    private String oppositeUserSex;
    private FirebaseAuth mAuth;
    private String currentUID;

    private DatabaseReference usersDb;

    ListView listView;
    List<Cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        usersDb = FirebaseDatabase.getInstance().getReference().child("users");

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            currentUID = currentUser.getUid();
            checkUserSex();
        } else {
            Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            finish();
        }

        initialization();
    }

    private void checkUserSex() {
        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUID)) {
                    String name = snapshot.child(currentUID).child("name").getValue(String.class);
                    if (snapshot.child(currentUID).child("sex").getValue() != null) {
                        userSex = snapshot.child(currentUID).child("sex").getValue().toString();
                        oppositeUserSex = userSex.equals("male") ? "female" : "male";
                        Toast.makeText(MainActivity.this, "User is " + userSex + ": " + name, Toast.LENGTH_SHORT).show();
                        getOppositeUserSex();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getOppositeUserSex() {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("sex").getValue() != null) {
                    if (snapshot.exists() && !snapshot.child("connections").child("nope").hasChild(currentUID) &&
                            !snapshot.child("connections").child("yeps").hasChild(currentUID) &&
                            snapshot.child("sex").getValue().toString().equals(oppositeUserSex)) {

                        String profileImageUrl = "default";
                        if (snapshot.child("profileImageUrl").getValue() != null && !snapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                        }

                        Cards item = new Cards(snapshot.getKey(), snapshot.child("name").getValue().toString(), profileImageUrl);
                        Log.d("MainActivity", "Cards" + item);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initialization() {
        rowItems = new ArrayList<>();

        arrayAdapter = new ArrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUID).setValue(true);
                Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUID).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {}

            @Override
            public void onScroll(float scrollProgressPercent) {}
        });

        flingContainer.setOnItemClickListener((itemPosition, dataObject) ->
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show());
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionDb = usersDb.child(currentUID).child("connections").child("yeps").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "New connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    usersDb.child(snapshot.getKey()).child("connections").child("matches").child(currentUID).child("chatID").setValue(key);
                    usersDb.child(currentUID).child("connections").child("matches").child(snapshot.getKey()).child("chatID").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
    }
}
