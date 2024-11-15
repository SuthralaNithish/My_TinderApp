package com.example.mytinderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolder> {
    private List<MatchesObject> matchesList;
    private Context context;
    public MatchesAdapter (List<MatchesObject> matchesList,Context context){
        this.matchesList = matchesList;
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
        matchesViewHolder.mMatchId.setText(matchesList.get(Position).getUserId());
        matchesViewHolder.mMatchName.setText(matchesList.get(Position).getName());
        if (!matchesList.get(Position).getProfileImageUrl().equals("default")){
        Glide.with(context).load(matchesList.get(Position).getProfileImageUrl()).into(matchesViewHolder.mMatchImage);
        }
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
