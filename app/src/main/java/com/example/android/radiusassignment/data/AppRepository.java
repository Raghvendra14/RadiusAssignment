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

    @Override
    public Flowable<BaseResponse> getData() {
        // TODO: get data from local source first, if available. Otherwise, fetch from remote source, store and show it in view
        Flowable<BaseResponse> localDataSource = mAppLocalDataSource.getData();

        return localDataSource.flatMap(baseResponse -> {
            if (baseResponse != null && baseResponse.getFacilityList() != null && baseResponse.getExclusionList() != null &&
                    !baseResponse.getFacilityList().isEmpty()) {
                return Flowable.just(baseResponse);
            } else if (InternetConnectivity.isConnected()) {
                return getRemoteDataSource();
            } else {
                return sendNoInternetException();
            }
        });
    }

    private Flowable<BaseResponse> sendNoInternetException() {
        return Flowable.<BaseResponse>error(new NoInternetException())
                .subscribeOn(Schedulers.from(TaskExecutor.threadPoolExecutor));
    }

    private Flowable<BaseResponse> getRemoteDataSource() {
        return mAppRemoteDataSource.getData()
                .flatMap(baseResponse -> {
                    mAppLocalDataSource.saveData(Boolean.TRUE, baseResponse);
                    return Flowable.just(baseResponse);
                }).subscribeOn(Schedulers.from(TaskExecutor.threadPoolExecutor));
    }

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
