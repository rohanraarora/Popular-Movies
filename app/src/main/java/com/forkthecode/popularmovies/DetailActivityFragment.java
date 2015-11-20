package com.forkthecode.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import extras.Constant;
import model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    ImageView posterImageView;
    TextView titleTextView;
    TextView releaseDateTextView;
    TextView ratingsTextView;
    TextView overviewTextView;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        posterImageView = (ImageView)view.findViewById(R.id.posterImageView);
        titleTextView = (TextView)view.findViewById(R.id.titleTextView);
        releaseDateTextView = (TextView)view.findViewById(R.id.releaseDateTextView);
        ratingsTextView = (TextView)view.findViewById(R.id.ratingTextView);
        overviewTextView = (TextView)view.findViewById(R.id.overviewTextView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Movie movie = (Movie)getArguments().getSerializable(Constant.MOVIE_INTENT_KEY);
        setData(movie);
    }

    public void setData(Movie movie){
        titleTextView.setText(movie.getTitle());
        releaseDateTextView.setText(movie.getReleaseDate());
        overviewTextView.setText(movie.getPlot());
        ratingsTextView.setText(movie.getUserRatings() + "/10");
        String imagePath = Constant.MOVIE_POSTER_BASE_URL + Constant.POSTER_SIZE_W185 + movie.getPosterPath();
        Picasso.with(getActivity()).load(imagePath).into(posterImageView);
    }



}
