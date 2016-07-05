package com.forkthecode.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forkthecode.popularmovies.data.MovieOpenHelper;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;
import extras.Constant;
import model.Movie;
import model.Review;
import model.Trailer;

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
    @Bind(R.id.favoriteImageView) ImageView favoriteImageView;

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
        Bundle arguments = getArguments();
        if(arguments!=null && arguments.containsKey(Constant.MOVIE_INTENT_KEY)) {
            Movie movie = (Movie) getArguments().getSerializable(Constant.MOVIE_INTENT_KEY);
            setData(movie);
        }
    }

    public void setData(final Movie movie){
        releaseDateTextView.setText(movie.getReleaseDate());
        overviewTextView.setText(movie.getPlot());
        ratingsTextView.setText(String.format("%s/10", movie.getUserRatings()));
        final MovieOpenHelper openHelper = MovieOpenHelper.getInstance(getActivity());
        if(openHelper.isMovieFavorite(movie.getId())){
            favoriteImageView.setImageResource(android.R.drawable.btn_star_big_on);
        }
        else {
            favoriteImageView.setImageResource(android.R.drawable.btn_star_big_off);
        }
        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!openHelper.isMovieFavorite(movie.getId())){
                    favoriteImageView.setImageResource(android.R.drawable.btn_star_big_on);
                    openHelper.addMovieAsFavorite(movie);
                }
                else {
                    favoriteImageView.setImageResource(android.R.drawable.btn_star_big_off);
                    openHelper.removeMovieAsFavorite(movie.getId());
                }
            }
        });
        String imagePath = Constant.MOVIE_POSTER_BASE_URL + Constant.POSTER_SIZE_W185 + movie.getPosterPath();
        String coverPath = Constant.MOVIE_POSTER_BASE_URL + Constant.COVER_SIZE_W342 + movie.getCoverImagePath();
        Picasso.with(getActivity()).load(imagePath).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(posterImageView);
        Picasso.with(getActivity()).load(coverPath).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(coverImageView);
        FetchTrailers.TrailersFetched listener = new FetchTrailers.TrailersFetched() {
            @Override
            public void onTrailersFetched(ArrayList<Trailer> trailers) {
                if (!isDetached()) {
                    Context context = getActivity();
                    if(context!=null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        for (final Trailer trailer : trailers) {
                            View view = inflater.inflate(R.layout.trailer_layout, trailerLinearLayout, false);
                            ImageView imageView = (ImageView) view.findViewById(R.id.trailerImageView);
                            Picasso.with(getActivity()).load(trailer.getTrailerThumbUrl())
                                    .error(R.drawable.placeholder).into(imageView);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException ex) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                                        startActivity(intent);
                                    }
                                }
                            });
                            if (trailerLinearLayout != null) {
                                trailerLinearLayout.addView(view);
                            } else {
                                break;
                            }
                        }
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
                    Context context = getActivity();
                    if(context!=null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        for (Review review : reviews) {
                            View view = inflater.inflate(R.layout.review_layout, reviewsLinearLayout, false);
                            TextView authorTextView = (TextView) view.findViewById(R.id.reviewAuthorTextView);
                            TextView contentTextView = (TextView) view.findViewById(R.id.reviewContentTextView);
                            authorTextView.setText(review.getAuthor());
                            contentTextView.setText(review.getContent());
                            if (reviewsLinearLayout != null) {
                                reviewsLinearLayout.addView(view);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        };
        FetchReviews task = new FetchReviews(getActivity(),movie,reviewListener);
        task.execute();
    }



}
