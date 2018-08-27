package com.example.android.radiusassignment.data.remote;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface ApiInterface {
    /**
     * API to GET facility and exclusion lists
     */
    @GET("iranjith4/ad-assignment/db")
    Flowable<BaseResponse> getData();
}
