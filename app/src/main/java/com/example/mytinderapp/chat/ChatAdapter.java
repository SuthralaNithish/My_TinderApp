package com.example.mytinderapp.chat;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mytinderapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private List<ChatObject> ChatList;
    private Context context;
    private String chatId = "";

    public ChatAdapter(List<ChatObject> chatList, Context context) {
        this.ChatList = chatList;
        this.context = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_chat, null, false);
        ViewGroup.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolder rcv = new ChatViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder chatViewHolder, int position) {
        chatViewHolder.mMessage.setText(ChatList.get(position).getMessage());
        final ChatObject chatObject = ChatList.get(position);

        if (chatObject.getCurrentUser()) {
            chatViewHolder.mMessage.setGravity(Gravity.END);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#404040"));
            chatViewHolder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
        } else {
            chatViewHolder.mMessage.setGravity(Gravity.START);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            chatViewHolder.mContainer.setBackgroundColor(Color.parseColor("#2DB4C8"));
        }

        chatViewHolder.itemView.setOnClickListener(v -> {
            showDeleteMessageDialog(chatObject.getMessageId(), position);
            //return true;
        });
    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }

    private void showDeleteMessageDialog(String messageId, int position) {
        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteMessageFromDatabase(messageId, position);
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void deleteMessageFromDatabase(String messageId, int position) {
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("Chat").child(chatId).child(messageId);
        messageRef.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ChatList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
