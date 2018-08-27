package com.example.android.radiusassignment.data.local;

import android.support.annotation.Nullable;

import com.example.android.radiusassignment.data.AppDataSource;
import com.example.android.radiusassignment.data.remote.BaseResponse;

import io.reactivex.Flowable;

public class AppLocalDataSource implements AppDataSource {
    @Nullable
    private static AppLocalDataSource __instance;

    public static AppLocalDataSource getInstance() {
        if (__instance == null)
            synchronized (AppLocalDataSource.class) {
                if (__instance == null) {
                    __instance = new AppLocalDataSource();
                }
            }
        return __instance;
    }

    // Prevent direct instantiation.
    private AppLocalDataSource() {
    }

    @Override
    public Flowable<BaseResponse> getData() {
        // TODO: get Data from Realm database
        return null;
    }
}
