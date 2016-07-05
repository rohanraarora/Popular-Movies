package com.forkthecode.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import extras.Constant;
import model.Movie;

/**
 * Created by Rohan on 2/2/2016.
 */
public class FetchTrailers extends AsyncTask<String,Void,ArrayList<String>> {

    public interface TrailersFetched{
        void onTrailersFetched(ArrayList<String> keys);
    }

    Context context;
    Movie movie;
    TrailersFetched trailersFetched;

    public FetchTrailers(Context context,Movie movie,TrailersFetched listner){
        this.context = context;
        this.trailersFetched = listner;
        this.movie = movie;
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
        if(!isOnline(context)) {
            cancel(true);
        }
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> keys = new ArrayList<>();
        try {
            URL url = new URL(Constant.getTrailersUrl(movie.getId()));
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String responseJSONString =  response.body().string();
            if(response.isSuccessful()) {
                JSONObject responseJSON = new JSONObject(responseJSONString);
                JSONArray resultJSONArray = responseJSON.getJSONArray("results");
                for(int i = 0;i<resultJSONArray.length();i++){
                    JSONObject trailerJSON = resultJSONArray.getJSONObject(i);
                    String key = trailerJSON.getString("key");
                    keys.add(key);
                }
                return keys;
            }
            else{
                Log.v("response", "code not 200");
                return null;
            }



        }
        catch (Exception e){
            Log.v("response",e.toString());
            return null;
        }

    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        if(trailersFetched!=null){
            trailersFetched.onTrailersFetched(strings);
        }
    }
}
