package com.se_project.movie_db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

public class ListViewCardAdapter extends RecyclerView.Adapter<ListViewCardAdapter.ViewHolder>{
    List<Movie> list;

    public ListViewCardAdapter(List<Movie> movies) {
        this.list = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card_view, parent, false);
        return new ListViewCardAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.movie = list.get(position);
        holder.movieName.setText(holder.movie.getTitle());
        holder.rankTextView.setText("#" + (position+1));
        holder.ratingTextView.setText(holder.movie.getRating() + "/10");
        new DownloadImageTask(holder.moviePoster).execute(holder.movie.getImagePoster());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView moviePoster;
        TextView movieName;
        TextView rankTextView;
        TextView ratingTextView;
        Movie movie;

        public ViewHolder(View item) {
            super(item);
            moviePoster = item.findViewById(R.id.card_image_poster_view);
            movieName = item.findViewById(R.id.list_card_movie_name);
            rankTextView = item.findViewById(R.id.rank_list_text_view);
            ratingTextView = item.findViewById(R.id.rating_text_view);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
