package com.forkthecode.popularmovies;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import extras.Constant;
import extras.PosterGridAdapter;
import model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayList<Movie> movies;
    PosterGridAdapter adapter;
    GridView gridView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View output = inflater.inflate(R.layout.fragment_main, container, false);
        movies = new ArrayList<>();
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
        gridView = (GridView)output.findViewById(R.id.gridView);

        return output;

    }

    class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            try {
                URL url = new URL(Constant.POPULAR_MOVIES_LIST_BASE_URL + Constant.API_KEY);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if(connection.getResponseCode() == 200) {
                    ArrayList<Movie> list = new ArrayList<>();
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    String response = responseBuilder.toString();
                    Log.v("json", response);
                    JSONObject responseJSON = new JSONObject(response);
                    JSONArray resultJSONArray = responseJSON.getJSONArray("results");
                    for(int i = 0;i<resultJSONArray.length();i++){
                        JSONObject movieJSON = resultJSONArray.getJSONObject(i);
                        long id = movieJSON.getLong("id");
                        String title = movieJSON.getString("title");
                        String plot = movieJSON.getString("overview");
                        String releaseDate = movieJSON.getString("release_date");
                        String posterPath = movieJSON.getString("poster_path");
                        Double userRatings = movieJSON.getDouble("vote_average");
                        Movie movie = new Movie(id,title,plot,releaseDate,posterPath,userRatings);
                        list.add(movie);
                    }
                    return list;
                }
                else{
                    Log.v("response","code not 200");
                    return null;
                }
            }
            catch (Exception e){
                Log.v("response",e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieList) {
            movies = movieList;
            adapter = new PosterGridAdapter(getActivity(),movies);
            gridView.setAdapter(adapter);
        }
    }
}
