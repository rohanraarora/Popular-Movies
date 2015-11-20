package com.forkthecode.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Collections;
import java.util.Comparator;

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
    ProgressDialog progressDialog;
    Comparator<Movie> popularityComparator;
    Comparator<Movie> ratingsComparator;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View output = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        popularityComparator = new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                return (int)(rhs.getPopularity()*1000 - lhs.getPopularity()*1000);
            }
        };

        ratingsComparator = new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                return (int)(rhs.getUserRatings()*1000 - lhs.getUserRatings()*1000);
            }
        };

        movies = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
        gridView = (GridView)output.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailActivityIntent = new Intent(getActivity(),DetailActivity.class);
                Movie item = movies.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.MOVIE_INTENT_KEY,item);
                detailActivityIntent.putExtras(bundle);
                startActivity(detailActivityIntent);
            }
        });

        return output;

    }

    class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

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
                        Double popularity = movieJSON.getDouble("popularity");
                        Movie movie = new Movie(id,title,plot,releaseDate,posterPath,userRatings,popularity);
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
            progressDialog.dismiss();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_ratings) {
            Collections.sort(movies,ratingsComparator);
            adapter.notifyDataSetChanged();
            return true;
        }
        else if (id == R.id.action_sort_popularity){
            Collections.sort(movies,popularityComparator);
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
