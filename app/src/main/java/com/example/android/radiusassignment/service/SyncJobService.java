package com.example.android.radiusassignment.service;

import com.example.android.radiusassignment.data.local.AppLocalDataSource;
import com.example.android.radiusassignment.data.remote.AppRemoteDataSource;
import com.example.android.radiusassignment.utils.InternetConnectivity;
import com.example.android.radiusassignment.utils.PrettyLogger;
import com.example.android.radiusassignment.utils.TaskExecutor;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SyncJobService extends JobService {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    @Override
    public boolean onStartJob(JobParameters job) {
        InternetConnectivity.init(this.getApplicationContext());
        if (InternetConnectivity.isConnected()) {
            mCompositeDisposable.clear();
            AppRemoteDataSource appRemoteDataSource = AppRemoteDataSource.getInstance();
            Disposable disposable = appRemoteDataSource.getData()
                    .subscribeOn(Schedulers.from(TaskExecutor.threadPoolExecutor))
                    .observeOn(Schedulers.io())
                    .subscribe(baseResponse -> {
                        if (baseResponse != null && baseResponse.getFacilityList() != null &&
                                baseResponse.getExclusionList() != null && !baseResponse.getFacilityList().isEmpty()) {
                            AppLocalDataSource.getInstance().saveData(null, baseResponse);
                        }
                    }, throwable -> {
                        // error while fetching data.
                    });

            mCompositeDisposable.add(disposable);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        mCompositeDisposable.clear();
        return false;
    }
}
