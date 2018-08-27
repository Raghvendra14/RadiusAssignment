package com.example.android.radiusassignment.utils;

import com.example.android.radiusassignment.data.AppRepository;
import com.example.android.radiusassignment.data.local.AppLocalDataSource;
import com.example.android.radiusassignment.data.remote.AppRemoteDataSource;

public class Injection {
    public static AppRepository provideAppRepository() {
        return AppRepository.getInstance(AppRemoteDataSource.getInstance(),
                AppLocalDataSource.getInstance());
    }
}
