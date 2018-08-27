package com.example.android.radiusassignment.filter;

import com.example.android.radiusassignment.BaseContract;

public interface FilterContract {
    interface View extends BaseContract.BaseView<Presenter> {
        void setLoadingIndicator(boolean active);
    }

    interface Presenter extends BaseContract.BasePresenter {
        void loadData(boolean showLoadingUI);
    }
}
