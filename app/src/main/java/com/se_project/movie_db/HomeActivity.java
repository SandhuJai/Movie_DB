package com.se_project.movie_db;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    /*
    Search Bar Contents
     */
    private EditText searchBar;
    private Button SearchBtn;
    private String searchString;
    private Button UserBtn;
    private ProgressDialog searchResultsLoading;
    private List<Movie> searchResults;

    private RecyclerView coming_soon_recyclerView;
    private RecyclerView.Adapter coming_soon_adapter;
    private RecyclerView.LayoutManager coming_soon_layout_manager;
    private List<Movie> comingSoonMovies;
    private Button coming_soon_movies_see_all_button;

    private RecyclerView in_theatres_recyclerView;
    private RecyclerView.Adapter in_theatres_adapter;
    private RecyclerView.LayoutManager in_theatres_layout_manager;
    private List<Movie> inTheatersMovies;
    private Button in_theatre_movies_see_all_button;

    private RecyclerView popular_movies_recyclerView;
    private RecyclerView.Adapter popular_movies_adapter;
    private RecyclerView.LayoutManager popular_movies_layout_manager;
    private List<Movie> popularMovies;
    private Button popular_movies_see_all_button;

    private RecyclerView top_rated_movies_recyclerView;
    private RecyclerView.Adapter top_rated_movies_adapter;
    private RecyclerView.LayoutManager top_rated_movies_layout_manager;
    private List<Movie> top_rated_Movies;
    private Button top_rated_movies_see_all_button;

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

    // Sends Intent to MovieDetailsActivity when clicked
    public void sendIntent(Context context, Movie movie) {
        if(movie != null) {
            TemporaryActivity.movie = movie;
            context.startActivity(new Intent(context, TemporaryActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*
        Set Search Bar
         */
        searchBar = findViewById(R.id.searchBar_Home);
        SearchBtn = findViewById(R.id.search_btn_home);
        UserBtn = findViewById(R.id.user_btn_home);
        searchResults = new ArrayList<>();
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textOnSearchBar = searchBar.getText().toString();

                if(textOnSearchBar == null) {
                    Toast.makeText(HomeActivity.this, "Please Enter Movie Name", Toast.LENGTH_LONG).show();
                }else if (textOnSearchBar.equalsIgnoreCase("")) {
                    Toast.makeText(HomeActivity.this, "Please Enter Movie Name", Toast.LENGTH_LONG).show();
                }else {
                    searchString = getSearchString(textOnSearchBar);
                    new GetSearchResults().execute();
                }
            }
        });

        comingSoonMovies = new ArrayList<>();
        coming_soon_recyclerView = findViewById(R.id.coming_soon_movies_recycler_view);
        coming_soon_recyclerView.setHasFixedSize(true);
        coming_soon_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        coming_soon_recyclerView.setLayoutManager(coming_soon_layout_manager);
        comingSoonRunning = true;
        coming_soon_movies_see_all_button = findViewById(R.id.coming_soon_see_all);

        new GetComingSoon().execute();

        inTheatersMovies = new ArrayList<>();
        in_theatres_recyclerView = findViewById(R.id.in_theatres_movies_recycler_view);
        in_theatres_recyclerView.setHasFixedSize(true);
        in_theatres_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        in_theatres_recyclerView.setLayoutManager(in_theatres_layout_manager);
        inTheaterRunning = true;
        in_theatre_movies_see_all_button = findViewById(R.id.in_theatres_see_all);

        new GetInTheater().execute();

        popularMovies = new ArrayList<>();
        popular_movies_recyclerView = findViewById(R.id.popular_movies_recycler_view);
        popular_movies_recyclerView.setHasFixedSize(true);
        popular_movies_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popular_movies_recyclerView.setLayoutManager(popular_movies_layout_manager);
        popularMoviesRunning = true;
        popular_movies_see_all_button = findViewById(R.id.popular_movies_see_all);

        new GetPopularMovies().execute();

        top_rated_Movies = new ArrayList<>();
        top_rated_movies_recyclerView = findViewById(R.id.top_rated_movies_recycler_view);
        top_rated_movies_recyclerView.setHasFixedSize(true);
        top_rated_movies_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        top_rated_movies_recyclerView.setLayoutManager(top_rated_movies_layout_manager);
        topRatedRunning = true;
        top_rated_movies_see_all_button = findViewById(R.id.top_rated_see_all);

        new GetTopRated().execute();

        setSeeAllButtons();
    }

    private String getSearchString(String text) {
        StringBuilder returnString = new StringBuilder("");

        for(int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if(ch == ' ')
                returnString.append('+');
            else {
                int in = (int) ch - 'a';

                if(in >= 0 && in <= 26) {
                    returnString.append(ch);
                }else {
                    in = (int) ch - 'A';

                    if(in >= 0 && in <= 26) {
                        returnString.append((ch + "").toUpperCase());
                    }
                }
            }
        }

        return returnString.toString();
    }

    /*
    Create class that searches
     */
    private class GetSearchResults extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchResultsLoading = new ProgressDialog(HomeActivity.this);
            searchResultsLoading.setMessage("Getting Search Results....");
            searchResultsLoading.setCancelable(false);
            searchResultsLoading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(searchResultsLoading.isShowing())    searchResultsLoading.dismiss();
            searchBar.setText("");

            if(searchResults.size() > 0) {
                sendIntent(HomeActivity.this, searchResults.get(0));
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeActivity.this, "Couldn't find the movie", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String movieName = searchString;

            String userRequest = "https://api.themoviedb.org/3/search/movie?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&query=" + movieName;

            String jsonStr = sh.makeServiceCall(userRequest);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for(int i = 0; i < Math.min(1, jsonArray.length()); i++) {
                        JSONObject temp = jsonArray.getJSONObject(i);

                        String title = temp.getString("title");
                        String imagePoster = "https://image.tmdb.org/t/p/w500" + temp.getString("poster_path");
                        String id = temp.getString("id");
                        String releaseDate = temp.getString("release_date");
                        String rating = temp.getString("vote_average");
                        String imageWallpaper = "";

                        if(searchResults.size() > 0) {
                            searchResults.set(0, new Movie(id, title, releaseDate, rating, imagePoster, imageWallpaper));
                        }else {
                            searchResults.add(new Movie(id, title, releaseDate, rating, imagePoster, imageWallpaper));
                        }
                    }

                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json Parsing Error", Toast.LENGTH_LONG).show();
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

    // Add onClickListeners to all See All Buttons so that clicking them sends Intent to ListActivity
    private void setSeeAllButtons() {
        coming_soon_movies_see_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListActivity.titleText = "Coming Soon";
                ListActivity.pageCnt = 0;
                startActivity(new Intent(HomeActivity.this, ListActivity.class));
            }
        });

        in_theatre_movies_see_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListActivity.titleText = "In Theatres";
                ListActivity.pageCnt = 0;
                startActivity(new Intent(HomeActivity.this, ListActivity.class));
            }
        });

        popular_movies_see_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListActivity.titleText = "Most Popular Movies";
                ListActivity.pageCnt = 0;
                startActivity(new Intent(HomeActivity.this, ListActivity.class));
            }
        });

        top_rated_movies_see_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListActivity.titleText = "Top Rated Movies";
                ListActivity.pageCnt = 0;
                startActivity(new Intent(HomeActivity.this, ListActivity.class));
            }
        });
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
            coming_soon_adapter = new CardAdapter(comingSoonMovies, HomeActivity.this);
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
                        String imagePoster = "https://image.tmdb.org/t/p/w500" + temp.getString("poster_path");
                        String id = temp.getString("id");
                        String releaseDate = temp.getString("release_date");
                        String rating = temp.getString("vote_average");
                        String imageWallpaper = "https://image.tmdb.org/t/p/w500" + temp.getString("backdrop_path");

                        comingSoonMovies.add(new Movie(id, title, releaseDate, rating, imagePoster, imageWallpaper));
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
            in_theatres_adapter = new CardAdapter(inTheatersMovies, HomeActivity.this);
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
                        String imagePoster = "https://image.tmdb.org/t/p/w500" + temp.getString("poster_path");
                        String id = temp.getString("id");
                        String releaseDate = temp.getString("release_date");
                        String rating = temp.getString("vote_average");
                        String imageWallpaper = "https://image.tmdb.org/t/p/w500" + temp.getString("backdrop_path");

                        inTheatersMovies.add(new Movie(id, title, releaseDate, rating, imagePoster, imageWallpaper));
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
            popular_movies_adapter = new CardAdapter(popularMovies, HomeActivity.this);
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
                        String imagePoster = "https://image.tmdb.org/t/p/w500" + temp.getString("poster_path");
                        String id = temp.getString("id");
                        String releaseDate = temp.getString("release_date");
                        String rating = temp.getString("vote_average");
                        String imageWallpaper = "https://image.tmdb.org/t/p/w500" + temp.getString("backdrop_path");

                        popularMovies.add(new Movie(id, title, releaseDate, rating, imagePoster, imageWallpaper));
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
            top_rated_movies_adapter = new CardAdapter(top_rated_Movies, HomeActivity.this);
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
                        String imagePoster = "https://image.tmdb.org/t/p/w500" + temp.getString("poster_path");
                        String id = temp.getString("id");
                        String releaseDate = temp.getString("release_date");
                        String rating = temp.getString("vote_average");
                        String imageWallpaper = "https://image.tmdb.org/t/p/w500" + temp.getString("backdrop_path");

                        top_rated_Movies.add(new Movie(id, title, releaseDate, rating, imagePoster, imageWallpaper));
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
