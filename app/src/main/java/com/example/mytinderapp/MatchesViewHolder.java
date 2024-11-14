package com.example.mytinderapp;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId;
    public MatchesViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
    }
    @Override
    public void onClick(View view) {

    }
}
