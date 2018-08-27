package com.example.android.radiusassignment.data;

import com.example.android.radiusassignment.data.remote.BaseResponse;

import io.reactivex.Flowable;

public interface AppDataSource {
    Flowable<BaseResponse> getData();

    void saveData(Boolean isOnlyStoredLocally, BaseResponse baseResponse);
}
