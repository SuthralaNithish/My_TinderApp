package com.example.mytinderapp.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytinderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private EditText mSendEditText;
    private ImageButton mSendButton;
    private String currentUserID, matchId, chatId;

    DatabaseReference mDatabaseUser, mDatabaseChat;
    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("connections").child("matches").child(matchId).child("chatID");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(resultChat, ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        getChatId();

        mSendButton = findViewById(R.id.Send);
        mSendEditText = findViewById(R.id.message);

        mSendButton.setOnClickListener(view -> sendMessage());
    }

   private void sendMessage() {
       String sendMessageText = mSendEditText.getText().toString();
       if (!sendMessageText.isEmpty()){
           DatabaseReference newMessageDb = mDatabaseChat.push();

           Map newMessage = new HashMap();
           newMessage.put("createdByUser", currentUserID);
           newMessage.put("text", sendMessageText);

           newMessageDb.setValue(newMessage).addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   mSendEditText.setText(null);
               } else {
                   Toast.makeText(ChatActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
               }
           });
       }
   }

    private void getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chatId = snapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                if (snapshot.exists()) {
                    String message = null;
                    String createdByUser = null;
                    String messageId = snapshot.getKey();

                    if (snapshot.child("text").getValue() != null) {
                        message = snapshot.child("text").getValue().toString();
                    }
                    if (snapshot.child("createdByUser").getValue() != null) {
                        createdByUser = snapshot.child("createdByUser").getValue().toString();
                    }

                    if (message != null && createdByUser != null && messageId != null) {
                        boolean currentUserBoolean = createdByUser.equals(currentUserID);
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean, messageId);
                        resultChat.add(newMessage);
                        if (mChatAdapter != null) {
                            mChatAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /*public void deleteMessage(String messageId) {
        mDatabaseChat.child(messageId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ChatActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < resultChat.size(); i++) {
                        if (resultChat.get(i).getMessageId().equals(messageId)) {
                            resultChat.remove(i);
                            mChatAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Failed to delete message", Toast.LENGTH_SHORT).show());
    }*/
}
