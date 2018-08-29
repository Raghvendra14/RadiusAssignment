package com.example.android.radiusassignment.interfaces;

public interface Constants {
    // Realm Related Constants
    String REALM_FILE_NAME = "radius.realm";
    int SCHEMA_VERSION = 0;

    // API Related Constants
    String APPLICATION_JSON = "application/json";
    String HEADER_CONTENT_TYPE = "Content-Type";

    // Preferences Related Constants/Keys
    String INTERNET_CONNECTIVITY_KEY = "INTERNET_CONNECTIVITY_KEY";

    // Error Related Constants
    int NO_INTERNET_ERROR = 0;
    int IO_ERROR = 1;
    int SOCKET_TIMEOUT_ERROR = 2;
    int OTHER_ERROR = 3;

    // Sync Job Service Related Constant
    int PLAY_SERVICES_CHECK = 5000;
    String SYNC_TAG = "sync-job-service";
    int WINDOW_START = 86400;
    int WINDOW_END = 87300;
}
