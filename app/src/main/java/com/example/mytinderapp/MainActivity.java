package com.example.mytinderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Cards cards;

    private arrayAdapter arrayAdapter;
    private int i;
    private String userSex ;
    private String oppositeUserSex;
    private FirebaseAuth mAuth;
    private String currentUID;

    private DatabaseReference usersDb;


    private DatabaseReference usersRef;

    ListView listView;
    List<Cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDb = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        initialization();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUID = currentUser.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
           checkUserSex();


    }

    private void checkUserSex() {
        usersRef.child("male").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUID)) {
                    String name = snapshot.child(currentUID).child("name").getValue(String.class);
                    userSex = "male";
                    oppositeUserSex = "female";
                    Toast.makeText(MainActivity.this, "User is Male: " + name, Toast.LENGTH_SHORT).show();
                    getOppositeUserSex();
                } else {
                    //checkFemaleGender(userId);
                    userSex = "female";
                    oppositeUserSex = "male";
                    getOppositeUserSex();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB_ERROR", "Error fetching user data: " + error.getMessage());
            }
        });
    }

    private void getOppositeUserSex () {
        usersRef.child(oppositeUserSex).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !snapshot.child("connections").child("nope").hasChild(currentUID) && !snapshot.child("connections").child("yeps").hasChild(currentUID))
                    for (DataSnapshot childSnapshot: snapshot.getChildren()){
                    Log.d("MainActivity", "" + childSnapshot);
                    String name = childSnapshot.child("name").getValue(String.class);
                        if (name != null) {
                            //Cards item = new Cards(snapshot.getKey(), name);
                            String oppositeUserId = snapshot.getKey();
                            String profileImageUrl = childSnapshot.child("imageUrl").getValue(String.class);
                            Cards item = new Cards(oppositeUserId, name, profileImageUrl);
                            Log.d("MainActivity",item.getUserId());
                            rowItems.add(item);
                            arrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("MainActivity", "Name is null for user: " + childSnapshot.getKey());
                        }
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB_ERROR", "Error fetching user data: " + error.getMessage());
            }
        });
    }



    private void initialization() {


        rowItems = new ArrayList<Cards>();

            arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

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
                usersDb.child(oppositeUserSex).child(userId).child("connections").child("nope").child(currentUID).setValue(true);
                Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(oppositeUserSex).child(userId).child("connections").child("yeps").child(currentUID).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }
            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionDb = usersDb.child(userSex).child(currentUID).child("connections").child("yeps").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "new connection", Toast.LENGTH_LONG).show();
                    usersDb.child(oppositeUserSex).child(snapshot.getKey()).child("connections").child("matches").child(currentUID).setValue(true);
                    usersDb.child(userSex).child(currentUID).child("connections").child("matches").child(snapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
        return;
    }
}