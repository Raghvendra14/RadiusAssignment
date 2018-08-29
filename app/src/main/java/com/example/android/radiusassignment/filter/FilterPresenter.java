package com.example.android.radiusassignment.filter;

import android.support.annotation.NonNull;

import com.example.android.radiusassignment.data.AppRepository;
import com.example.android.radiusassignment.data.remote.BaseResponse;
import com.example.android.radiusassignment.interfaces.Constants;
import com.example.android.radiusassignment.utils.NoInternetException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link FilterActivityFragment}), retrieves the data and updates the
 * UI as required.
 */
public class FilterPresenter implements FilterContract.Presenter {
    @NonNull
    private final AppRepository mAppRepository;

    @NonNull
    private final FilterContract.View mFilterView;

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public FilterPresenter(@NonNull AppRepository appRepository,
                           @NonNull FilterContract.View filterView) {
        mAppRepository = checkNotNull(appRepository, "App Repository cannot be null");
        mFilterView = checkNotNull(filterView, "Filter View cannot be null");

        mCompositeDisposable = new CompositeDisposable();
        mFilterView.setPresenter(this);
    }

    /**
     *  Method called to subscribe to presenter
     */
    @Override
    public void subscribe() {
        loadData(true);
    }

    /**
     *  Method called to unsubscribe to presenter
     */
    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    /**
     *  @param showLoadingUI Pass in true to display loading screen in the UI
     */
    @Override
    public void loadData(final boolean showLoadingUI) {
        if (showLoadingUI) {
            mFilterView.setLoadingIndicator(true);
        }

        // clear all previous disposables
        mCompositeDisposable.clear();
        Disposable disposable = mAppRepository
                .getData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    processFilters(baseResponse);
                    mFilterView.setLoadingIndicator(false);
                }, throwable -> {
                    mFilterView.setLoadingIndicator(false);
                    handleThrowable(throwable);
                });
        // add the current disposable
        mCompositeDisposable.add(disposable);
    }

    /**
     *  This is to process base response {@link BaseResponse} as per its values
     *  @param baseResponse baseResponse Object
     */
    private void processFilters(BaseResponse baseResponse) {
        if (baseResponse == null || baseResponse.getFacilityList() == null ||
                baseResponse.getExclusionList() == null ||
                baseResponse.getFacilityList().isEmpty()) {
            // no facilities found, show empty view screen
            mFilterView.showEmptyView();
        } else {
            // show the list of facilities
            mFilterView.showFacilities(baseResponse);
        }
    }

    /**
     *  It is handle throwable based on its instance
     *  @param throwable Throwable that is used to show error in UI.
     */
    private void handleThrowable(@NonNull Throwable throwable) {
        checkNotNull(throwable);
        // check its instance
        if (throwable instanceof NoInternetException) {
            mFilterView.showApiErrors(Constants.NO_INTERNET_ERROR);
        } else if (throwable instanceof SocketTimeoutException) {
            mFilterView.showApiErrors(Constants.SOCKET_TIMEOUT_ERROR);
        } else if (throwable instanceof IOException){
            mFilterView.showApiErrors(Constants.IO_ERROR);
        } else {
            mFilterView.showApiErrors(Constants.OTHER_ERROR);
        }
    }
}
