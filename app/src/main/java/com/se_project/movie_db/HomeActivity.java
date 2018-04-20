package com.se_project.movie_db;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Movie> popularMovies;
    private static final int initialResults = 50;
    private ProgressDialog progressDialog;

    private String TAG = HomeActivity.class.getSimpleName();

    private static String popular_movies = "https://api.themoviedb.org/3/movie/popular?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&language=en-US&page=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        popularMovies = new ArrayList<>();

        recyclerView = findViewById(R.id.popular_movies_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        new GetPopularMovies().execute();
    }

    private class GetPopularMovies extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("Loading Results...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing())  progressDialog.dismiss();
            adapter = new CardAdapter(popularMovies);
            recyclerView.setAdapter(adapter);
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
}
