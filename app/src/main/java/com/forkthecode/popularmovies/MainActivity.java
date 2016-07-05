package com.forkthecode.popularmovies;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import extras.Constant;
import model.Movie;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    @Nullable
    @Bind(R.id.container)
    FrameLayout container;

    Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(container!=null){
            mTwoPane = true;
            if (savedInstanceState == null) {
                DetailActivityFragment detailActivityFragment = new DetailActivityFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                detailActivityFragment.setArguments(getIntent().getExtras());

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, detailActivityFragment).commit();
                return;
            }

            // Create a new Fragment to be placed in the activity layout

        }
        else {
            mTwoPane = false;
        }

    }


    @Override
    public void onItemSelected(Movie movie) {
        if(mTwoPane){
            DetailActivityFragment newFragment = new DetailActivityFragment();
            Bundle args = new Bundle();
            args.putSerializable(Constant.MOVIE_INTENT_KEY,movie);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else {
            Intent detailActivityIntent = new Intent(this,DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MOVIE_INTENT_KEY,movie);
            detailActivityIntent.putExtras(bundle);
            startActivity(detailActivityIntent);
        }
    }
}
