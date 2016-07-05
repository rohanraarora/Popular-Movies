package model;

/**
 * Created by rohanarora on 06/07/16.
 */
public class Trailer {
    private long movieId;
    private String key;

    public Trailer(long movieId,String key){
        this.movieId = movieId;
        this.key = key;
    }

    public long getMovieId() {
        return movieId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getTrailerThumbUrl(){
        return "http://img.youtube.com/vi/" + key + "/hqdefault.jpg";
    }
}
