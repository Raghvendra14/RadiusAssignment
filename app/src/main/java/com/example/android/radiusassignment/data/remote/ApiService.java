package com.example.android.radiusassignment.data.remote;

import com.example.android.radiusassignment.BuildConfig;
import com.example.android.radiusassignment.interfaces.Constants;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ApiService {
    private static final String baseUrl = BuildConfig.BASE_URL;

    /**
     * It is called to get the Retrofit instance after inserting interceptors in it
     *
     * @return Retrofit instance using Retrofit.Builder {@link Retrofit.Builder}
     */
    public static Retrofit getAdapter() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(15, SECONDS)
                .readTimeout(20, SECONDS)
                .writeTimeout(20, SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    // create a requset builder object to add interceptor
                    Request.Builder requestBuilder = original.newBuilder()
                            .method(original.method(), original.body());

                    HttpUrl urlBuilder = original.url();
                    // add content type inside headers, in case of POST or PUT methods
                    if (original.method().equals("POST") || original.method().equals("PUT")) {
                        requestBuilder.header(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
                    }

                    // add HttpUrl in request builder
                    requestBuilder.url(urlBuilder);

                    // build request object from request builder
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                });

        // adding http logging interceptor while debugging
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(interceptor);
        }

        // create an okhttpClient object
        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        // create and return a retrofit instance using Retrofit.Builder
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}

