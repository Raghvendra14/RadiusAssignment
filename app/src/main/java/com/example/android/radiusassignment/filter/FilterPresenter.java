package com.example.android.radiusassignment.filter;

import android.support.annotation.NonNull;

import com.example.android.radiusassignment.data.AppRepository;
import com.example.android.radiusassignment.data.remote.BaseResponse;
import com.example.android.radiusassignment.utils.PrettyLogger;
import com.example.android.radiusassignment.utils.TaskExecutor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    @Override
    public void subscribe() {
        loadData(true);
    }

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

        mCompositeDisposable.clear();
        Disposable disposable = mAppRepository
                .getData()
                .subscribeOn(Schedulers.from(TaskExecutor.threadPoolExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    PrettyLogger.json(new Gson().toJson(baseResponse, new TypeToken<BaseResponse>(){}.getType()));
                }, throwable -> {
                    // TODO: handle db and remote errors here
                    PrettyLogger.d(throwable.getMessage());
                });
        mCompositeDisposable.add(disposable);
    }
}
