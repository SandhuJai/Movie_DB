package com.se_project.movie_db;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView coming_soon_recyclerView;
    private RecyclerView.Adapter coming_soon_adapter;
    private RecyclerView.LayoutManager coming_soon_layout_manager;
    private List<Movie> comingSoonMovies;

    private RecyclerView in_theatres_recyclerView;
    private RecyclerView.Adapter in_theatres_adapter;
    private RecyclerView.LayoutManager in_theatres_layout_manager;
    private List<Movie> inTheatersMovies;

    private RecyclerView popular_movies_recyclerView;
    private RecyclerView.Adapter popular_movies_adapter;
    private RecyclerView.LayoutManager popular_movies_layout_manager;
    private List<Movie> popularMovies;

    private RecyclerView top_rated_movies_recyclerView;
    private RecyclerView.Adapter top_rated_movies_adapter;
    private RecyclerView.LayoutManager top_rated_movies_layout_manager;
    private List<Movie> top_rated_Movies;

    private boolean comingSoonRunning, inTheaterRunning, popularMoviesRunning, topRatedRunning;

    private static final int initialResults = 50;
    private ProgressDialog progressDialog;

    private String TAG = HomeActivity.class.getSimpleName();

    private static String coming_soon_movies = "https://api.themoviedb.org/3/movie/upcoming?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&language=en-US&page=1&region=US";
    private static String in_theater_movies = "https://api.themoviedb.org/3/movie/now_playing?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&language=en-US&page=1&region=US";
    private static String popular_movies = "https://api.themoviedb.org/3/movie/popular?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&language=en-US&page=1&region=US";
    private static String top_rated_movies = "https://api.themoviedb.org/3/movie/top_rated?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&language=en-US&page=1&region=US";

    // tells if the process calling should start/stop progress dialog or not
    private boolean shouldStartStop(int index) {
        switch (index) {
            case 0 :
                if(comingSoonRunning && !inTheaterRunning && !popularMoviesRunning && !topRatedRunning) {
                    return true;
                }
                return false;
            case 1 :
                if(!comingSoonRunning && inTheaterRunning && !popularMoviesRunning && !topRatedRunning) {
                    return true;
                }
                return false;
            case 2 :
                if(!comingSoonRunning && !inTheaterRunning && popularMoviesRunning && !topRatedRunning) {
                    return true;
                }
                return false;
            case 3 :
                if(!comingSoonRunning && !inTheaterRunning && !popularMoviesRunning && topRatedRunning) {
                    return true;
                }
                return false;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        comingSoonMovies = new ArrayList<>();
        coming_soon_recyclerView = findViewById(R.id.coming_soon_movies_recycler_view);
        coming_soon_recyclerView.setHasFixedSize(true);
        coming_soon_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        coming_soon_recyclerView.setLayoutManager(coming_soon_layout_manager);
        comingSoonRunning = true;

        new GetComingSoon().execute();

        inTheatersMovies = new ArrayList<>();
        in_theatres_recyclerView = findViewById(R.id.in_theatres_movies_recycler_view);
        in_theatres_recyclerView.setHasFixedSize(true);
        in_theatres_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        in_theatres_recyclerView.setLayoutManager(in_theatres_layout_manager);
        inTheaterRunning = true;

        new GetInTheater().execute();

        popularMovies = new ArrayList<>();
        popular_movies_recyclerView = findViewById(R.id.popular_movies_recycler_view);
        popular_movies_recyclerView.setHasFixedSize(true);
        popular_movies_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popular_movies_recyclerView.setLayoutManager(popular_movies_layout_manager);
        popularMoviesRunning = true;

        new GetPopularMovies().execute();

        top_rated_Movies = new ArrayList<>();
        top_rated_movies_recyclerView = findViewById(R.id.top_rated_movies_recycler_view);
        top_rated_movies_recyclerView.setHasFixedSize(true);
        top_rated_movies_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        top_rated_movies_recyclerView.setLayoutManager(top_rated_movies_layout_manager);
        topRatedRunning = true;

        new GetTopRated().execute();
    }

    private class GetComingSoon extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(shouldStartStop(0)) {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Loading Results....");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(shouldStartStop(0)) if(progressDialog.isShowing())  progressDialog.dismiss();
            comingSoonRunning = false;
            coming_soon_adapter = new CardAdapter(comingSoonMovies);
            coming_soon_recyclerView.setAdapter(coming_soon_adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(coming_soon_movies);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting Name
                    JSONArray results = jsonObj.getJSONArray("results");

                    for(int i = 0; i < results.length(); i++) {
                        JSONObject temp = results.getJSONObject(i);

                        String title = temp.getString("original_title");

                        comingSoonMovies.add(new Movie(title));
                    }
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

    private class GetInTheater extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(shouldStartStop(1)) {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Loading Results....");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(shouldStartStop(1)) if(progressDialog.isShowing())  progressDialog.dismiss();
            in_theatres_adapter = new CardAdapter(inTheatersMovies);
            in_theatres_recyclerView.setAdapter(in_theatres_adapter);
            inTheaterRunning = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(in_theater_movies);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting Name
                    JSONArray results = jsonObj.getJSONArray("results");

                    for(int i = 0; i < results.length(); i++) {
                        JSONObject temp = results.getJSONObject(i);

                        String title = temp.getString("original_title");

                        inTheatersMovies.add(new Movie(title));
                    }
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

    private class GetPopularMovies extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(shouldStartStop(2)) {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Loading Results....");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(shouldStartStop(2)) if(progressDialog.isShowing())  progressDialog.dismiss();
            popular_movies_adapter = new CardAdapter(popularMovies);
            popular_movies_recyclerView.setAdapter(popular_movies_adapter);
            popularMoviesRunning = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(popular_movies);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting Name
                    JSONArray results = jsonObj.getJSONArray("results");

                    for(int i = 0; i < results.length(); i++) {
                        JSONObject temp = results.getJSONObject(i);

                        String title = temp.getString("original_title");

                        popularMovies.add(new Movie(title));
                    }
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

    private class GetTopRated extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(shouldStartStop(3)) {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Loading Results....");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(shouldStartStop(3)) if(progressDialog.isShowing())  progressDialog.dismiss();
            top_rated_movies_adapter = new CardAdapter(top_rated_Movies);
            top_rated_movies_recyclerView.setAdapter(top_rated_movies_adapter);
            topRatedRunning = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(top_rated_movies);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting Name
                    JSONArray results = jsonObj.getJSONArray("results");

                    for(int i = 0; i < results.length(); i++) {
                        JSONObject temp = results.getJSONObject(i);

                        String title = temp.getString("original_title");

                        top_rated_Movies.add(new Movie(title));
                    }
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
}
