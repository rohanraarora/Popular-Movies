package com.forkthecode.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import extras.Constant;
import model.Movie;
import model.Review;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    @Bind(R.id.posterImageView) ImageView posterImageView;
    @Bind(R.id.coverImageView) ImageView coverImageView;
    @Bind(R.id.releaseDateTextView) TextView releaseDateTextView;
    @Bind(R.id.ratingTextView) TextView ratingsTextView;
    @Bind(R.id.overviewTextView)TextView overviewTextView;
    @Bind(R.id.trailerLinearLayout) LinearLayout trailerLinearLayout;
    @Bind(R.id.reviewsLinearLayout) LinearLayout reviewsLinearLayout;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Movie movie = (Movie)getArguments().getSerializable(Constant.MOVIE_INTENT_KEY);
        setData(movie);
    }

    public void setData(Movie movie){
        releaseDateTextView.setText(movie.getReleaseDate());
        overviewTextView.setText(movie.getPlot());
        ratingsTextView.setText(movie.getUserRatings() + "/10");
        String imagePath = Constant.MOVIE_POSTER_BASE_URL + Constant.POSTER_SIZE_W185 + movie.getPosterPath();
        String coverPath = Constant.MOVIE_POSTER_BASE_URL + Constant.COVER_SIZE_W342 + movie.getCoverImagePath();
        Picasso.with(getActivity()).load(imagePath).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(posterImageView);
        Picasso.with(getActivity()).load(coverPath).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(coverImageView);
        FetchTrailers.TrailersFetched listener = new FetchTrailers.TrailersFetched() {
            @Override
            public void onTrailersFetched(ArrayList<String> keys) {
                if (!isDetached()) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    for (final String key : keys) {
                        View view = inflater.inflate(R.layout.trailer_layout, null);
                        ImageView imageView = (ImageView) view.findViewById(R.id.trailerImageView);
                        Picasso.with(getActivity()).load(Constant.getTrailerThumbUrl(key))
                                .error(R.drawable.placeholder).into(imageView);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                                    startActivity(intent);
                                } catch (ActivityNotFoundException ex) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://www.youtube.com/watch?v=" + key));
                                    startActivity(intent);
                                }
                            }
                        });
                        trailerLinearLayout.addView(view);
                    }
                }
            }

        };
        FetchTrailers trialerTask = new FetchTrailers(getActivity(),movie,listener);
        trialerTask.execute();

        FetchReviews.ReviewsFetched reviewListener = new FetchReviews.ReviewsFetched() {
            @Override
            public void onReviewsFetched(ArrayList<Review> reviews) {
                if(!isDetached()) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    for (Review review : reviews) {
                        View view = inflater.inflate(R.layout.review_layout, null);
                        TextView authorTextView = (TextView) view.findViewById(R.id.reviewAuthorTextView);
                        TextView contentTextView = (TextView) view.findViewById(R.id.reviewContentTextView);
                        authorTextView.setText(review.getAuthor());
                        contentTextView.setText(review.getContent());
                        reviewsLinearLayout.addView(view);
                    }
                }
            }
        };
        FetchReviews task = new FetchReviews(getActivity(),movie,reviewListener);
        task.execute();
    }



}
