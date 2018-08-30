package com.example.android.radiusassignment.filter;

import android.support.annotation.NonNull;

import com.example.android.radiusassignment.BaseContract;
import com.example.android.radiusassignment.data.remote.BaseResponse;

import javax.annotation.Nullable;

public interface FilterContract {
    interface View extends BaseContract.BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showApiErrors(int errorType);

        void showEmptyView();

        void showFacilities();
    }

    interface Presenter extends BaseContract.BasePresenter {
        void loadData(boolean showLoadingUI);

        @Nullable BaseResponse getBaseResponse();

        void itemClicked(boolean isSelected, @NonNull String facilityId, @NonNull String optionId);
    }
}
