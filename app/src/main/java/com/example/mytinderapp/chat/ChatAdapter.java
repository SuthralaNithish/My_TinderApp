package com.example.mytinderapp.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytinderapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private List<ChatObject> ChatList;
    private Context context;
    private String chatId = "";
    private ChatActivity chatActionListener;

    public ChatAdapter(List<ChatObject> chatList, Context context, ChatActivity listener) {
        this.ChatList = chatList;
        this.context = context;
        this.chatActionListener = listener;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_chat, null, false);
        layoutView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ChatViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder chatViewHolder, int position) {
        ChatObject chatObject = ChatList.get(position);
        chatViewHolder.mMessage.setText(chatObject.getMessage());

        if (chatObject.getCurrentUser()) {
            chatViewHolder.mMessage.setGravity(Gravity.END);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#404040"));
            chatViewHolder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
        } else {
            chatViewHolder.mMessage.setGravity(Gravity.START);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            chatViewHolder.mContainer.setBackgroundColor(Color.parseColor("#2DB4C8"));
        }

        chatViewHolder.itemView.setOnClickListener(v -> showEditDeleteDialog(
                chatObject.getMessageId(),
                position,
                chatObject.getMessage()));
    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }

    public void setChatId(String id) {
        chatId = id;
    }

    private void showEditDeleteDialog(String messageId, int position, String currentMessage) {
        new AlertDialog.Builder(context)
                .setTitle("Message Options")
                .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditMessageDialog(messageId, position, currentMessage);
                    } else if (which == 1) {
                        deleteMessageFromDatabase(messageId, position);
                    }
                })
                .show();
    }

    private void showEditMessageDialog(String messageId, int position, String currentMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Message");

        final EditText input = new EditText(context);
        input.setText(currentMessage);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedMessage = input.getText().toString();
            if (!updatedMessage.isEmpty()) {
                chatActionListener.onMessageEdit(messageId, updatedMessage, position);
            } else {
                Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
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
