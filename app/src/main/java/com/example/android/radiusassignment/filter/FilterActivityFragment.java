package com.example.android.radiusassignment.filter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.radiusassignment.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fragment to hold filter screen.
 */
public class FilterActivityFragment extends Fragment implements FilterContract.View {
    private FilterContract.Presenter mPresenter;

    public FilterActivityFragment() {
        // Requires empty public constructor
    }

    public static FilterActivityFragment newInstance() {
        return new FilterActivityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull FilterContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (active) {
            // show Loading UI
        } else {
            // hide Loading UI
        }
    }
}
