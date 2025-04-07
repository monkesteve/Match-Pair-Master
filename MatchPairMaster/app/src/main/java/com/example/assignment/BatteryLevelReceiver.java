package com.example.assignment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryLevelReceiver extends BroadcastReceiver {
    public static final String ACTION_BATTERY_LOW = "com.example.assignment.ACTION_BATTERY_LOW";
    public static final String ACTION_BATTERY_OKAY = "com.example.assignment.ACTION_BATTERY_OKAY";
    public static final String EXTRA_BATTERY_LEVEL = "com.example.assignment.EXTRA_BATTERY_LEVEL";

    private static final int LOW_BATTERY_THRESHOLD = 20;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level != -1 && scale != -1) {
                int batteryPct = (int) ((level / (float) scale) * 100);

                // Broadcast the battery level to activities and service
                Intent batteryIntent = new Intent();
                batteryIntent.putExtra(EXTRA_BATTERY_LEVEL, batteryPct);

                if (batteryPct <= LOW_BATTERY_THRESHOLD) {
                    batteryIntent.setAction(ACTION_BATTERY_LOW);
                } else {
                    batteryIntent.setAction(ACTION_BATTERY_OKAY);
                }

                context.sendBroadcast(batteryIntent);

                // Also update the foreground service
                Intent serviceIntent = new Intent(context, ForegroundService.class);
                serviceIntent.setAction("BATTERY_UPDATE");
                serviceIntent.putExtra(EXTRA_BATTERY_LEVEL, batteryPct);
                context.startService(serviceIntent);
            }
        }
    }
}
