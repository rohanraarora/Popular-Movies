package extras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.forkthecode.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.Movie;

/**
 * Created by Rohan on 11/19/2015.

 */
public class PosterGridAdapter extends BaseAdapter {

    ArrayList<Movie> movies;
    Context context;
    public PosterGridAdapter(Context context,ArrayList<Movie> movieList){
        movies = movieList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View output = convertView;
        if(output == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            output = inflater.inflate(R.layout.movie_poster_cell_layout,parent,false);
        }
        ImageView imageView = (ImageView)output.findViewById(R.id.cellLayoutImageView);
        Movie movie = getItem(position);
        String url = Constant.MOVIE_POSTER_BASE_URL + Constant.POSTER_SIZE_W185 + movie.getPosterPath();
        Picasso.with(context).load(url).into(imageView);
        return output;
    }
}
