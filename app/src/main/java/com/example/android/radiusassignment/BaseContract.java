package com.example.android.radiusassignment;

public interface BaseContract {
    interface BasePresenter {
        void subscribe();

        void unsubscribe();
    }

    interface BaseView<T> {
        void setPresenter(T presenter);
    }
}
