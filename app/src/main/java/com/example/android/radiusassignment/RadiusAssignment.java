package com.example.android.radiusassignment;

import android.app.Application;
import android.content.ContextWrapper;

import com.example.android.radiusassignment.data.remote.AppRemoteDataSource;
import com.example.android.radiusassignment.interfaces.Constants;
import com.example.android.radiusassignment.utils.AppPreference;
import com.example.android.radiusassignment.utils.InternetConnectivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RadiusAssignment extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // initialize Realm
        Realm.init(this);

        // Create a new realm file "radius.realm", in this case, using RealmConfiguration
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Constants.REALM_FILE_NAME)
                .schemaVersion(Constants.SCHEMA_VERSION)
                .build();
        // set default Realm Configuration
        Realm.setDefaultConfiguration(realmConfiguration);

        /* Initialize the Preference class */
        new AppPreference.Builder().setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        // Initialize AppRemoteDataSource for first time
        AppRemoteDataSource.getInstance();

        // Internet Connectivity initiation
        InternetConnectivity.init(this);
    }
}
