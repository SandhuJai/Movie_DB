package com.se_project.movie_db;

import android.os.AsyncTask;

public class SearchClass {
    private String query;

    public Movie performSearch(String query) {
        this.query = query;

        return null;
    }

    /*
    *   Async Task to get Search Results
     */
    private class GetResults extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
