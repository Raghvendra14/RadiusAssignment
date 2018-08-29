package com.example.android.radiusassignment.filter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.radiusassignment.R;
import com.example.android.radiusassignment.data.remote.BaseResponse;
import com.example.android.radiusassignment.interfaces.Constants;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fragment to hold filter screen.
 */
public class FilterActivityFragment extends Fragment implements FilterContract.View {
    private FilterContract.Presenter mPresenter;

    private Snackbar mSnackbar;

    @BindView(R.id.json_view)
    TextView mJSONView;

    @BindView(R.id.progress_dialog)
    ProgressBar mProgressDialog;

    private int mShortAnimationDuration;

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
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, rootView);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        return rootView;
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
        // TODO: create a loading layout
        if (active) {
            // show Loading UI
            setupCrossfade();
        } else {
            // hide Loading UI
            performCrossfade();
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
                getView() == null) {
            return;
        }

        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }

        mSnackbar = Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_LONG).setAction(R.string.try_again, view -> {
            mPresenter.loadData(true);
        });
        mSnackbar.show();

    }

    @Override
    public void showEmptyView() {
        // TODO: create an empty view in layout
    }

    @Override
    public void showFacilities(@NonNull final BaseResponse baseResponse) {
        // TODO: show the data in Fields
        checkNotNull(baseResponse);
        mJSONView.setText(new Gson().toJson(baseResponse));
    }

    private void setupCrossfade() {
        // Initially hide the content view.
        mJSONView.setVisibility(View.GONE);
        mProgressDialog.setVisibility(View.VISIBLE);
        // reset opacity
        mJSONView.setAlpha(1f);
        mProgressDialog.setAlpha(1f);
    }

    private void performCrossfade() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mJSONView.setAlpha(0f);
        mJSONView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mJSONView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mProgressDialog.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressDialog.setVisibility(View.GONE);
                    }
                });
    }
}
