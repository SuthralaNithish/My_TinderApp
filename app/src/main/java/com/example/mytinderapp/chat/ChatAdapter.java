package com.example.mytinderapp.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mytinderapp.MatchesObject;
import com.example.mytinderapp.MatchesViewHolder;
import com.example.mytinderapp.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private List<ChatObject> ChatList;
    private Context context;

    public ChatAdapter(List<ChatObject> matchesList, Context context) {
        this.ChatList = matchesList;
        this.context = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_chat, null, false);
        ViewGroup.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolder rcv = new ChatViewHolder((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder  chatViewHolder, int position) {
        chatViewHolder.mMessage.setText(ChatList.get(position).getMessage());
        if (ChatList.get(position).getCurrentUser()){
            chatViewHolder.mMessage.setGravity(Gravity.END);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#404040"));
            chatViewHolder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }else {
            chatViewHolder.mMessage.setGravity(Gravity.START);
            chatViewHolder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            chatViewHolder.mContainer.setBackgroundColor(Color.parseColor("#2DB4C8"));
        }


    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }
}
