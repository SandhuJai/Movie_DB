package com.se_project.movie_db;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class MovieDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static Movie movie;

    // Youtube API key
    public static final String API_KEY = "AIzaSyBVGc2rlkt2e4XMirRjc3EVFxmS1DKR_WE";
    public static String MOVIE_ID;

    private ArrayList<String> desc;

    private TextView titleTextView;
    private TextView yearTextView;
    private TextView ratingTextView;
    private TextView descriptionTextView;
    private ImageView posterImageView;

    private YouTubePlayerView youTubePlayerView;

    // TODO: Add Wishlist Button
    private Button addWishlist;

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {

            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onStopped() {

            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        });

        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });

        if(!wasRestored) {
            youTubePlayer.cueVideo(MOVIE_ID);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Trailer could not be loaded correctly.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        titleTextView = findViewById(R.id.title_text_view_movie_details);
        yearTextView = findViewById(R.id.year_text_view_movie_details);
        ratingTextView = findViewById(R.id.rating_text_view_movie_details);
        descriptionTextView = findViewById(R.id.movie_description_movie_details);
        posterImageView = findViewById(R.id.movie_poster_image_view_movie_details);

        youTubePlayerView = findViewById(R.id.trailerPlayerView);
        youTubePlayerView.initialize(API_KEY, this);

        desc = new ArrayList<>();

        new GetDescription().execute();

        new DownloadImageTask(posterImageView).execute(movie.getImagePoster());

        titleTextView.setText(movie.getTitle());
        yearTextView.setText(movie.getReleaseDate());
        ratingTextView.setText(movie.getRating());
        descriptionTextView.setText(movie.getID());

    }

    private class GetDescription extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.i("Post", "" + desc.size());

            descriptionTextView.setText(desc.get(0));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String request = "https://api.themoviedb.org/3/movie/" + movie.getID() + "?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&language=en-US";

            String jsonStr = sh.makeServiceCall(request);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting Name
                    String link = jsonObj.getString("overview");
                    desc.add(link);
                } catch (JSONException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "JSON parsing error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
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
