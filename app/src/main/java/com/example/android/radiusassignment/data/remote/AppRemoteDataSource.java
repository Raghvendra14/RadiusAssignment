package com.example.android.radiusassignment.data.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.radiusassignment.data.AppDataSource;

import io.reactivex.Flowable;
import retrofit2.Retrofit;

import static com.google.common.base.Preconditions.checkNotNull;

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

    /**
     * Method to get data from remote source using Retrofit2 Client.
     *
     * @return Flowable of type BaseResponse
     */
    @Override
    public Flowable<BaseResponse> getData() {
        return mApiInterface.getData();
    }

    /**
     * Method to store data in remote data source, i.e., backend.
     *
     * @param isOnlyStoredLocally Boolean value to decide where to store the data.
     * @param baseResponse        BaseResponse object that needs to be stored
     */
    @Override
    public void saveData(@Nullable Boolean isOnlyStoredLocally,
                         @NonNull BaseResponse baseResponse) {
        // to store data in the backend
        checkNotNull(baseResponse);
    }
}
