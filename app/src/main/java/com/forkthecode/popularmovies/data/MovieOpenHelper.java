package com.forkthecode.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import model.Movie;

import static com.forkthecode.popularmovies.data.MovieContract.*;

/**
 * Created by rohanarora on 06/07/16.
 *
 */
public class MovieOpenHelper extends SQLiteOpenHelper {

    static MovieOpenHelper movieOpenHelper;

    private MovieOpenHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    public static MovieOpenHelper getInstance(Context context) {
        if(movieOpenHelper == null){
            movieOpenHelper = new MovieOpenHelper(context);
        }
        return movieOpenHelper;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MOVIE_TABLE_NAME + " ( " + MOVIE_ID_COLUMN
                + " INTEGER PRIMARY KEY, " + MOVIE_TITLE_COLUMN + " TEXT, " + MOVIE_PLOT_COLUMN + " TEXT, "
                + MOVIE_COVER_IMAGE_PATH_COLUMN + " TEXT, " + MOVIE_POPULARITY_COLUMN + " REAL, "
                + MOVIE_POSTER_PATH_COLUMN + " TEXT, " + MOVIE_RELEASE_DATE_COLUMN + " TEXT, "
                + MOVIE_USER_RATINGS_COLUMN + " REAL);");

    }

    public long addMovieAsFavorite(Movie movie){
        SQLiteDatabase db = movieOpenHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MOVIE_ID_COLUMN,movie.getId());
        cv.put(MOVIE_COVER_IMAGE_PATH_COLUMN,movie.getCoverImagePath());
        cv.put(MOVIE_PLOT_COLUMN,movie.getPlot());
        cv.put(MOVIE_POPULARITY_COLUMN,movie.getPopularity());
        cv.put(MOVIE_POSTER_PATH_COLUMN,movie.getPosterPath());
        cv.put(MOVIE_RELEASE_DATE_COLUMN,movie.getReleaseDate());
        cv.put(MOVIE_TITLE_COLUMN,movie.getTitle());
        cv.put(MOVIE_USER_RATINGS_COLUMN,movie.getUserRatings());
        long id = db.insert(MOVIE_TABLE_NAME,null,cv);
        return id;
    }

    public int removeMovieAsFavorite(long movieId){
        SQLiteDatabase db = movieOpenHelper.getWritableDatabase();
        return db.delete(MOVIE_TABLE_NAME,MOVIE_ID_COLUMN + " = " + movieId,null);
    }

    public boolean isMovieFavorite(long movieId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(MOVIE_TABLE_NAME,null,
                MOVIE_ID_COLUMN + " = " + movieId, null,null,null,null);
        return c.getCount() > 0;
    }

    public ArrayList<Movie> getAllMovies(){
        SQLiteDatabase db = movieOpenHelper.getReadableDatabase();
        Cursor c = db.query(MOVIE_TABLE_NAME,null,null,null,null,null,null);
        ArrayList<Movie> movies = new ArrayList<>();
        while (c.moveToNext()){
            long id = c.getLong(c.getColumnIndex(MOVIE_ID_COLUMN));
            String title = c.getString(c.getColumnIndex(MOVIE_TITLE_COLUMN));
            String plot = c.getString(c.getColumnIndex(MOVIE_PLOT_COLUMN));
            String posterPath = c.getString(c.getColumnIndex(MOVIE_POSTER_PATH_COLUMN));
            String coverImagePath = c.getString(c.getColumnIndex(MOVIE_COVER_IMAGE_PATH_COLUMN));
            String releaseDate = c.getString(c.getColumnIndex(MOVIE_RELEASE_DATE_COLUMN));
            Double userRating = c.getDouble(c.getColumnIndex(MOVIE_USER_RATINGS_COLUMN));
            Double popularity = c.getDouble(c.getColumnIndex(MOVIE_POPULARITY_COLUMN));
            Movie movie = new Movie(id,title,plot,releaseDate,posterPath,userRating,popularity,coverImagePath);
            movies.add(movie);
        }
        return movies;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
