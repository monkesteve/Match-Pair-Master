package com.example.assignment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {
    private Context context;
    private final int NOTIFICATION_ID = 1;
    private boolean isDestroyed = false;
    private Handler handler;
    private boolean isAppInForeground = true;
    private Runnable delayedNotificationUpdate;
    private int currentBatteryLevel = 100;
    public static final String ACTION_BATTERY_UPDATE = "BATTERY_UPDATE";

    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        handler = new Handler(Looper.getMainLooper());

        Notification notification = showNotification("We will remind you to play the game");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }

        // Create the delayed runnable
        delayedNotificationUpdate = () -> {
            updateNotification("It's been 10 seconds since you last played and we miss you already! " +
                    "Come back and play!");
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action != null) {
            switch (action) {
                case "APP_OPENED":
                    // App is in foreground
                    isAppInForeground = true;
                    // Remove any pending notifications
                    handler.removeCallbacks(delayedNotificationUpdate);
                    updateNotification("We will remind you to play the game");
                    break;

                case "APP_CLOSED":
                    // App is closed/background
                    isAppInForeground = false;
                    // Start the 10-second countdown
                    handler.removeCallbacks(delayedNotificationUpdate);
                    handler.postDelayed(delayedNotificationUpdate, 10000); // 10 seconds delay
                    break;

                case "BATTERY_UPDATE":
                    int batteryLevel = intent.getIntExtra(BatteryLevelReceiver.EXTRA_BATTERY_LEVEL, -1);
                    if (batteryLevel != -1) {
                        currentBatteryLevel = batteryLevel;
                        if (batteryLevel <= 20) {
                            updateNotification("Battery low (" + batteryLevel + "%)! Please charge your device to continue playing.");
                        } else if (!isAppInForeground) {
                            // Only update if app is not in foreground
                            updateNotification("We will remind you to play the game. Battery: " + batteryLevel + "%");
                        }
                    }
                    break;
            }
        }

        return START_STICKY;
    }

    private void updateNotification(String data) {
        Notification notification = showNotification(data);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification showNotification(String content) {
        String CHANNEL_ID = "100";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Remind To Play Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Match Pair game reminders");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create intent to open app when notification is tapped
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Match Pair Master")
                .setContentText(content)
                .setSmallIcon(R.drawable.baseline_sentiment_dissatisfied_24)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;

        // Remove any pending callbacks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        // Restart the service to keep it running
        Intent restartServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
        startService(restartServiceIntent);
    }
}