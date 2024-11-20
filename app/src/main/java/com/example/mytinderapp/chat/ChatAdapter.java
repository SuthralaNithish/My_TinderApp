package com.example.mytinderapp.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mytinderapp.MatchesObject;
import com.example.mytinderapp.MatchesViewHolder;
import com.example.mytinderapp.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<MatchesViewHolder> {
    private List<ChatObject> ChatList;
    private Context context;
    public ChatAdapter(List<ChatObject> matchesList, Context context){
        this.ChatList = matchesList;
        this.context = context;
    }
    @Override
    public MatchesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.item_matches,null, false);
        ViewGroup.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolder rcv = new MatchesViewHolder((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(MatchesViewHolder matchesViewHolder, int Position) {
    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }
}
