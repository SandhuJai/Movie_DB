package com.se_project.movie_db;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TemporaryActivity extends AppCompatActivity {
    public static Movie movie;

    // Youtube API key
    public static final String API_KEY = "AIzaSyBVGc2rlkt2e4XMirRjc3EVFxmS1DKR_WE";
    public static String MOVIE_ID;

    private ArrayList<String> links;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporary);

        links = new ArrayList<>();

        new GetTrailer().execute();
    }

    private class GetTrailer extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TemporaryActivity.this);
            progressDialog.setMessage("Loading Results....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progressDialog.isShowing())  progressDialog.dismiss();
            MOVIE_ID = links.get(0);

            MovieDetailsActivity.MOVIE_ID = MOVIE_ID;
            MovieDetailsActivity.movie = movie;

            startActivity(new Intent(TemporaryActivity.this, MovieDetailsActivity.class));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String request = "https://api.themoviedb.org/3/movie/" + movie.getID() + "/videos?api_key=5da5237ab7ea9020ac16bcd6e8ffe0ab&language=en-US";

            String jsonStr = sh.makeServiceCall(request);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting Name
                    JSONArray results = jsonObj.getJSONArray("results");

                    for(int i = 0; i < results.length(); i++) {
                        JSONObject temp = results.getJSONObject(i);

                        String link = temp.getString("key");

                        links.add(link);
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
