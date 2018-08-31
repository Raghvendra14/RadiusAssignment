package com.example.android.radiusassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.android.radiusassignment.filter.FilterActivity;
import com.example.android.radiusassignment.interfaces.Constants;
import com.example.android.radiusassignment.service.SyncJobService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // schedule the Firebase Job Scheduler
        scheduleJob();

        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check for play services dialog response
        if (requestCode == Constants.PLAY_SERVICES_CHECK) {
            if (resultCode == Activity.RESULT_OK) {
                // Try to schedule the job again.
                scheduleJob();
            } else {
                // display the play services unavailable message.
                showPSUnavailableMessage();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     *  It is called to check for play services availability.
     *  @return Play Services availability status
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, Constants.PLAY_SERVICES_CHECK)
                        .show();
            }
            showPSUnavailableMessage();
            return false;
        }
        return true;
    }

    /**
     *  Schedules the job using Firebase Job Dispatcher {@link FirebaseJobDispatcher}
     */
    private void scheduleJob() {
        if (isGooglePlayServicesAvailable()) {
            // create a firebase job dispatcher instance
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

            Job syncJob = dispatcher.newJobBuilder()
                    // the SyncJobService that will be called
                    .setService(SyncJobService.class)
                    // uniquely identifies the job
                    .setTag(Constants.SYNC_TAG)
                    // recurring job
                    .setRecurring(true)
                    // don't persist past a device reboot
                    .setLifetime(Lifetime.FOREVER)
                    // start between 1 day (86400 seconds) and 1.25 day (87300) seconds from now
                    .setTrigger(Trigger.executionWindow(Constants.WINDOW_START,  Constants.WINDOW_END))
                    // don't overwrite an existing job with the same tag
                    .setReplaceCurrent(false)
                    // retry with exponential backoff
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .build();

            // schedule the job in dispatcher
            dispatcher.mustSchedule(syncJob);
        }
    }

    /**
     *  Shows Play Services unavailability message.
     */
    private void showPSUnavailableMessage() {
        Toast.makeText(getApplicationContext(), R.string.error_play_services_unavailable, Toast.LENGTH_SHORT)
                .show();
    }
}
