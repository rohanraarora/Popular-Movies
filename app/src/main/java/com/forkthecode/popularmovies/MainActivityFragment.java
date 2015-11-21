package com.forkthecode.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;
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
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
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

        Context context;

        public FetchMoviesTask(Context context){
            this.context = context;
        }


        public boolean isOnline(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isOnline(context)){
                cancel(true);
                Toast.makeText(context,"No Internet Connection.",Toast.LENGTH_LONG).show();
            }
            else{
                progressDialog.show();
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            try {
                URL url = new URL(Constant.POPULAR_MOVIES_LIST_BASE_URL + Constant.API_KEY);
                OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseJSONString =  response.body().string();
                    if(response.isSuccessful()) {
                        ArrayList<Movie> list = new ArrayList<>();
                        JSONObject responseJSON = new JSONObject(responseJSONString);
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
