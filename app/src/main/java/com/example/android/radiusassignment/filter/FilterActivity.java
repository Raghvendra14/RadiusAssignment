package com.example.android.radiusassignment.filter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.radiusassignment.R;
import com.example.android.radiusassignment.utils.ActivityUtils;
import com.example.android.radiusassignment.utils.Injection;

public class FilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        FilterActivityFragment filterActivityFragment =
                (FilterActivityFragment) getSupportFragmentManager().findFragmentById(R.id.content_fragment);
        if (filterActivityFragment == null) {
            // Create the fragment
            filterActivityFragment = FilterActivityFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), filterActivityFragment,
                    R.id.content_fragment);
        }

        // create an instance of presenter
        new FilterPresenter(Injection.provideAppRepository(), filterActivityFragment);
    }

}
