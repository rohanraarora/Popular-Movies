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
import model.Review;

/**
 * Created by Rohan on 2/2/2016.
 */
public class FetchReviews extends AsyncTask<String,Void,ArrayList<Review>> {
    public interface ReviewsFetched{
        void onReviewsFetched(ArrayList<Review> keys);
    }

    Context context;
    Movie movie;
    ReviewsFetched reviewsFetched;

    public FetchReviews(Context context,Movie movie,ReviewsFetched listner){
        this.context = context;
        this.reviewsFetched = listner;
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
    protected ArrayList<Review> doInBackground(String... params) {
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            URL url = new URL(Constant.getReviewsUrl(movie.getId()));
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
                    JSONObject reviewJSON = resultJSONArray.getJSONObject(i);
                    String author = reviewJSON.getString("author");
                    String content = reviewJSON.getString("content");
                    Review review = new Review(author,content);
                    reviews.add(review);
                }
                return reviews;
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
    protected void onPostExecute(ArrayList<Review> reviews) {
        super.onPostExecute(reviews);
        if(reviewsFetched!=null){
            reviewsFetched.onReviewsFetched(reviews);
        }
    }
}
