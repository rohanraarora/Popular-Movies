package model;

import java.io.Serializable;

/**
 * Created by Rohan on 11/19/2015.
 *A simple movie POJO
 */
public class Movie implements Serializable{
    private long id;
    private String title;
    private String plot;
    private String releaseDate;
    private String posterPath;
    private Double userRatings;
    private Double popularity;

    public Movie(long id,String title,String plot,String releaseDate,String posterPath, Double userRatings,Double popularity){
        this.id = id;
        this.title = title;
        this.plot = plot;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.userRatings = userRatings;
        this.popularity = popularity;
    }

    public long getId() {
        return id;
    }

    public Double getUserRatings() {
        return userRatings;
    }

    public String getPlot() {
        return plot;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public Double getPopularity() {
        return popularity;
    }
}
