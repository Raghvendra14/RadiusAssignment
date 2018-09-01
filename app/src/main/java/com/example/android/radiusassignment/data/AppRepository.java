package com.example.android.radiusassignment.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.radiusassignment.data.remote.BaseResponse;
import com.example.android.radiusassignment.utils.InternetConnectivity;
import com.example.android.radiusassignment.utils.NoInternetException;
import com.example.android.radiusassignment.utils.TaskExecutor;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class AppRepository implements AppDataSource {
    @Nullable
    private static AppRepository __instance = null;

    @NonNull
    private final AppDataSource mAppRemoteDataSource;

    @NonNull
    private final AppDataSource mAppLocalDataSource;

    public static AppRepository getInstance(@NonNull AppDataSource appRemoteDataSource,
                                            @NonNull AppDataSource appLocalDataSource) {
        if (__instance == null)
            synchronized (AppRepository.class) {
                if (__instance == null) {
                    __instance = new AppRepository(appRemoteDataSource, appLocalDataSource);
                }
            }
        return __instance;
    }

    // Prevent direct instantiation.
    private AppRepository(@NonNull AppDataSource appRemoteDataSource,
                          @NonNull AppDataSource appLocalDataSource) {
        mAppRemoteDataSource = checkNotNull(appRemoteDataSource);
        mAppLocalDataSource = checkNotNull(appLocalDataSource);
    }

    /**
     * It is to fetch data from data source.
     * <p>Firstly, it finds the data in local source from Realm.IO {@link io.realm.Realm} database.
     * If found, it returns data to the consumer/caller. Otherwise, it checks for internet connectivity.</p>
     * <p>If internet connectivity is available, then it gets data from remote source using Retrofit2 Client {@link retrofit2.Retrofit}.
     * Otherwise, it throws a NoInternetException {@link NoInternetException} to the consumer/caller.</p>
     *
     * @return Flowable<BaseResponse> Flowable of BaseResponse {@link BaseResponse} type.
     */
    @Override
    public Flowable<BaseResponse> getData() {
        // fetch data from local data source
        Flowable<BaseResponse> localDataSource = mAppLocalDataSource.getData();

        return localDataSource.flatMap(baseResponse -> {
            if (baseResponse != null && baseResponse.getFacilityList() != null && baseResponse.getExclusionList() != null &&
                    !baseResponse.getFacilityList().isEmpty()) {
                return Flowable.just(baseResponse);
            } else if (InternetConnectivity.isConnected()) {
                // fetch data from remote data source
                return getRemoteDataSource();
            } else {
                return sendNoInternetException();
            }
        });
    }

    /**
     * Returns Flowable error of NoInternetException {@link NoInternetException}
     *
     * @see Flowable#error(Throwable)
     */
    private Flowable<BaseResponse> sendNoInternetException() {
        return Flowable.error(new NoInternetException());
    }

    /**
     * To get data from remote data source.<p>This is also responsible to store data in local data
     * source.This first calls the saveData() of AppRepository and it redirects to saveData() of
     * AppLocalDataSource.</p>
     *
     * @return Flowable<BaseResonse> Flowable of BaseResponse {@link BaseResponse} type.
     * @see com.example.android.radiusassignment.data.AppRepository#saveData(Boolean, BaseResponse)
     */
    private Flowable<BaseResponse> getRemoteDataSource() {
        return mAppRemoteDataSource.getData()
                .flatMap(baseResponse -> {
                    saveData(Boolean.TRUE, baseResponse);
                    return Flowable.just(baseResponse);
                }).subscribeOn(Schedulers.from(TaskExecutor.threadPoolExecutor));
    }

    /**
     * Stores data in data sources based on Boolean variable.
     * If it is null, stores it in remote data source as well as in local data source.
     * If it is Boolean.TRUE, stores it in local data source.
     * If it is Boolean.FALSE, stores it in remote data source.
     *
     * @param isOnlyStoreLocally Used to define place to store the data
     * @param baseResponse       BaseResponse object to store
     */
    @Override
    public void saveData(@Nullable Boolean isOnlyStoreLocally,
                         @NonNull BaseResponse baseResponse) {
        checkNotNull(baseResponse);
        if (isOnlyStoreLocally == null) {
            mAppLocalDataSource.saveData(null, baseResponse);
            mAppRemoteDataSource.saveData(null, baseResponse);
        } else if (isOnlyStoreLocally == Boolean.TRUE) {
            mAppLocalDataSource.saveData(null, baseResponse);
        } else {
            mAppRemoteDataSource.saveData(null, baseResponse);
        }
    }
}
