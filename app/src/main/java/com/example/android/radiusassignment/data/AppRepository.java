package com.example.android.radiusassignment.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.radiusassignment.data.remote.BaseResponse;
import com.example.android.radiusassignment.utils.InternetConnectivity;
import com.example.android.radiusassignment.utils.NoInternetException;

import io.reactivex.Flowable;

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

        if (InternetConnectivity.isConnected()) {
            return mAppRemoteDataSource.getData();
        } else {
            return Flowable.error(new NoInternetException());
        }
    }
}
