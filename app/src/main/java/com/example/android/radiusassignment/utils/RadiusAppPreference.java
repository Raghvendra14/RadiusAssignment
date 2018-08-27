package com.example.android.radiusassignment.utils;

import com.example.android.radiusassignment.interfaces.Constants;

/**
 *  This class uses AppPreference {@link AppPreference} to store values in Shared Preferences.
 */
public class RadiusAppPreference {
    public static void setInternetAvailable(boolean isInternetAvailable) {
        AppPreference.putBoolean(Constants.INTERNET_CONNECTIVITY_KEY, isInternetAvailable);
    }

    public static boolean isInternetAvailable() {
        return AppPreference.getBoolean(Constants.INTERNET_CONNECTIVITY_KEY, false);
    }
}
