package model;

/**
 * Created by Rohan on 11/19/2015.
 *
 */
public class Movie {
    private long id;
    private String title;
    private String plot;
    private String releaseDate;
    private String posterPath;
    private Double userRatings;

    public Movie(long id,String title,String plot,String releaseDate,String posterPath, Double userRatings){
        this.id = id;
        this.title = title;
        this.plot = plot;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.userRatings = userRatings;
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
}
