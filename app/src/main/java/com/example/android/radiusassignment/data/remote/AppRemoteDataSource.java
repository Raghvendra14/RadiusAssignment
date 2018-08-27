package com.example.android.radiusassignment.data.remote;

import android.support.annotation.Nullable;

import com.example.android.radiusassignment.data.AppDataSource;

import io.reactivex.Flowable;
import retrofit2.Retrofit;

public class AppRemoteDataSource implements AppDataSource {
    @Nullable
    private static AppRemoteDataSource __instance;

    private ApiInterface mApiInterface;

    public static AppRemoteDataSource getInstance() {
        if (__instance == null)
            synchronized (AppRemoteDataSource.class) {
                if (__instance == null) {
                    __instance = new AppRemoteDataSource();
                }
            }
        return __instance;
    }

    // Prevent direct instantiation.
    private AppRemoteDataSource() {
        Retrofit retrofit = ApiService.getAdapter();
        mApiInterface = retrofit.create(ApiInterface.class);
    }

    @Override
    public Flowable<BaseResponse> getData() {
        return mApiInterface.getData();
    }
}
