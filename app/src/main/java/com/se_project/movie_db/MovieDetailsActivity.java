package com.se_project.movie_db;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MovieDetailsActivity extends AppCompatActivity {
    public static Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Log.i("ID", movie.getID());
        Log.i("Title", movie.getTitle());
        Log.i("Release Date", movie.getReleaseDate());
        Log.i("Rating", movie.getRating());
        Log.i("Poster", movie.getImagePoster());
        Log.i("Wallpaper", movie.getImageWallpaper());
    }
}
