package com.example.android.radiusassignment.filter;

import com.example.android.radiusassignment.BaseContract;
import com.example.android.radiusassignment.data.remote.BaseResponse;

public interface FilterContract {
    interface View extends BaseContract.BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showApiErrors(int errorType);

        void showEmptyView();

        void showFacilities(BaseResponse baseResponse);
    }

    interface Presenter extends BaseContract.BasePresenter {
        void loadData(boolean showLoadingUI);
    }
}
