package com.example.android.radiusassignment.filter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.radiusassignment.R;
import com.example.android.radiusassignment.data.remote.BaseResponse;
import com.example.android.radiusassignment.interfaces.Constants;
import com.example.android.radiusassignment.interfaces.RecyclerClickListenerInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fragment to hold filter screen.
 */
public class FilterActivityFragment extends Fragment implements FilterContract.View,
        RecyclerClickListenerInterface {
    private FilterContract.Presenter mPresenter;

    private Snackbar mSnackbar;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.facilities_recycler_view)
    RecyclerView mFacilitiesRecyclerView;

    @BindView(R.id.empty_view)
    ConstraintLayout mEmptyView;

    private int mShortAnimationDuration;

    private View mRootView;
    private FilterRecyclerViewAdapter mFacilityRecyclerViewAdapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, mRootView);

        // setup short animation duration once
        setupShortAnimationDuration();

        // setup Facilities Recycler View components
        setupRecyclerView();

        return mRootView;
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
            showProgressBar();
        } else {
            // hide Loading UI
            hideProgressBar();
        }
    }

    @Override
    public void showApiErrors(final int errorType) {
        String errorMessage = null;
        switch (errorType) {
            case Constants.NO_INTERNET_ERROR:
                errorMessage = getString(R.string.no_internet_available);
                break;
            case Constants.SOCKET_TIMEOUT_ERROR:
                errorMessage = getString(R.string.socket_error);
                break;
            case Constants.IO_ERROR:
                errorMessage = getString(R.string.io_error);
                break;
            case Constants.OTHER_ERROR:
                errorMessage = getString(R.string.other_error);
                break;
            default:
                break;
        }

        if (errorMessage == null || TextUtils.isEmpty(errorMessage) ||
                mRootView == null) {
            return;
        }

        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }

        mSnackbar = Snackbar.make(mRootView, errorMessage, Snackbar.LENGTH_LONG).setAction(R.string.try_again, view -> {
            mPresenter.loadData(true);
        });
        mSnackbar.show();

    }

    @Override
    public void showEmptyView() {
        // show the empty view and hide the recycler view
        mEmptyView.setVisibility(View.VISIBLE);
        mFacilitiesRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showFacilities() {
        // hide the empty view and show the recycler view
        mEmptyView.setVisibility(View.GONE);
        mFacilitiesRecyclerView.setVisibility(View.VISIBLE);
        // get base response from presenter
        BaseResponse baseResponse = mPresenter.getBaseResponse();
        checkNotNull(baseResponse);
        if (mFacilityRecyclerViewAdapter != null) {
            mFacilityRecyclerViewAdapter.updateList(baseResponse.getFacilityList());
        }
    }

    @Override
    public void onItemClickListener(boolean isSelected, @NonNull String facilityId, @NonNull String optionId) {
        checkNotNull(facilityId);
        checkNotNull(optionId);
        mPresenter.itemClicked(isSelected, facilityId, optionId);
    }

    /**
     * Setup recycler view for content
     */
    private void setupRecyclerView() {
        mFacilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        if (getActivity() != null) {
            mFacilityRecyclerViewAdapter = new FilterRecyclerViewAdapter(getActivity(), new ArrayList<>(),
                    this);
            mFacilitiesRecyclerView.setAdapter(mFacilityRecyclerViewAdapter);
        }
    }

    private void setupShortAnimationDuration() {
        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    private void showProgressBar() {
        // Initially hide the content view.
        mEmptyView.setVisibility(View.GONE);
        mFacilitiesRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        // reset opacity
        mEmptyView.setAlpha(1f);
        mFacilitiesRecyclerView.setAlpha(1f);
        mProgressBar.setAlpha(1f);
    }

    private void hideProgressBar() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mEmptyView.setAlpha(0f);
        mEmptyView.setVisibility(View.VISIBLE);
        mFacilitiesRecyclerView.setAlpha(0f);
        mFacilitiesRecyclerView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mEmptyView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);
        mFacilitiesRecyclerView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mProgressBar.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }
}
