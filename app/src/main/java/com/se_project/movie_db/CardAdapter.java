package com.se_project.movie_db;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    List<Movie> list = new ArrayList<>();

    public CardAdapter(List<Movie> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.movie = list.get(position);
        holder.cardtext.setText(holder.movie.getTitle());
        holder.cardimage.setImageResource(R.drawable.loader);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cardimage;
        TextView cardtext;
        Movie movie;

        public ViewHolder(View item) {
            super(item);
            cardimage = item.findViewById(R.id.cardimage);
            cardtext = item.findViewById(R.id.cardtext);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
