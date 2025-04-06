package com.example.assignment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// This class is used to track the whole app's state (opened or closed) instead of just one activity
// and send this information to a foreground service for notification.
public abstract class BaseActivity extends AppCompatActivity {

    private static int activeActivities = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activeActivities++;
        // App is opened
        sendAppStateToService("APP_OPENED");
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeActivities--;

        // Only consider app closed when all activities are stopped
        if (activeActivities == 0) {
            sendAppStateToService("APP_CLOSED");
        }
    }

    protected void sendAppStateToService(String action) {
        Intent intent = new Intent(this, ForegroundService.class);
        intent.setAction(action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
}