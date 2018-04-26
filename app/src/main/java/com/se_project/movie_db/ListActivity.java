package com.se_project.movie_db;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    /*
    Search Bar Contents
     */
    private EditText searchBar;
    private Button SearchBtn;
    private String searchString;
    private Button UserBtn;
    private ProgressDialog searchResultsLoading;
    private List<Movie> searchResults;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private Button seeMore;

    private TextView titleOfPage;   // Title of Page comes here

    public static String titleText;
    private List<Movie> movies;

    public static int pageCnt;

    private StringBuilder query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        /*
        Set Search Bar
         */
        searchBar = findViewById(R.id.searchBar_List);
        SearchBtn = findViewById(R.id.search_btn_List);
        UserBtn = findViewById(R.id.user_btn_List);
        searchResults = new ArrayList<>();
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textOnSearchBar = searchBar.getText().toString();

                if(textOnSearchBar == null) {
                    Toast.makeText(ListActivity.this, "Please Enter Movie Name", Toast.LENGTH_LONG).show();
                }else if (textOnSearchBar.equalsIgnoreCase("")) {
                    Toast.makeText(ListActivity.this, "Please Enter Movie Name", Toast.LENGTH_LONG).show();
                }else {
                    searchString = getSearchString(textOnSearchBar);
                    new GetSearchResults().execute();
                }
            }
        });

        seeMore = findViewById(R.id.see_more_button);
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetList().execute();
            }
        });

        movies = new ArrayList<>();

        query = new StringBuilder("https://api.themoviedb.org/3/movie/");

        if(titleText.equalsIgnoreCase("Coming Soon")) {
            query.append("upcoming");
        }else if(titleText.equalsIgnoreCase("In Theatres")) {
            query.append("now_playing");
        }else if(titleText.equalsIgnoreCase("Most Popular Movies")) {
            query.append("popular");
        }else if(titleText.equalsIgnoreCase("Top Rated Movies")) {
            query.append("top_rated");
        }

        query.append("?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&language=en-US");

        titleOfPage = findViewById(R.id.listViewTitle);
        titleOfPage.setText(titleText);

        recyclerView = findViewById(R.id.generic_list_recycler_view);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        new GetList().execute();
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
            searchResultsLoading = new ProgressDialog(ListActivity.this);
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
                sendIntent(ListActivity.this, searchResults.get(0));
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ListActivity.this, "Couldn't find the movie", Toast.LENGTH_LONG).show();
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

    // Sends Intent to MovieDetailsActivity when clicked
    public void sendIntent(Context context, Movie movie) {
        if(movie != null) {
            TemporaryActivity.movie = movie;
            context.startActivity(new Intent(context, TemporaryActivity.class));
        }
    }

    private class GetList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pageCnt++;
            query.append("&page=" + pageCnt + "&region=US");
            progressDialog = new ProgressDialog(ListActivity.this);
            progressDialog.setMessage("Loading Results...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(pageCnt == 1) {
                adapter = new ListViewCardAdapter(movies, ListActivity.this);
                recyclerView.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }

            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(query.toString());

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

                        movies.add(new Movie(id, title, releaseDate, rating, imagePoster, imageWallpaper));
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
