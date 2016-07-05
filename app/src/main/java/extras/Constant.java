package extras;

import com.forkthecode.popularmovies.FetchTrailers;

import model.Movie;

/**
 * Created by Rohan on 11/19/2015.
 *
 */
public class Constant {

    public static final String API_KEY = "API+KEY";
    public static final String POPULAR_MOVIES_LIST_BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
    public static final String MOST_RATED_MOVIES_LIST_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
    public static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE_W185 = "w185";
    public static final String COVER_SIZE_W342 = "w342";
    public static final String FETCH_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static String getTrailersUrl(long id){
        return FETCH_BASE_URL + id + "/videos?api_key=" + API_KEY;
    }


    public static String getReviewsUrl(long id){
        return FETCH_BASE_URL + id + "/reviews?api_key=" + API_KEY;
    }
    public static final String MOVIE_INTENT_KEY = "movie_intent";
}
