package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private static int activeActivities = 0;
    protected WaifuButton waifuButton;

    // SharedPreferences keys and static values for saved button position
    private static final String PREFS_NAME = "WaifuButtonPrefs";
    private static final String KEY_BUTTON_X = "buttonX";
    private static final String KEY_BUTTON_Y = "buttonY";
    private static float lastX = -1;
    private static float lastY = -1;
    private BroadcastReceiver batteryReceiver;
    private boolean isLowBattery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        addWaifuButton();

        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BatteryLevelReceiver.ACTION_BATTERY_LOW.equals(action)) {
                    int level = intent.getIntExtra(BatteryLevelReceiver.EXTRA_BATTERY_LEVEL, -1);
                    isLowBattery = true;
                    Toast.makeText(BaseActivity.this,
                            "Battery low: " + level + "%. Please charge your device.",
                            Toast.LENGTH_LONG).show();
                } else if (BatteryLevelReceiver.ACTION_BATTERY_OKAY.equals(action)) {
                    isLowBattery = false;
                }
            }
        };
    }

    private void addWaifuButton() {
        // Get the root view where the button will be added.
        ViewGroup rootView = findViewById(android.R.id.content);

        // Create the WaifuButton instance.
        waifuButton = new WaifuButton(this);
        waifuButton.setId(R.id.fab);
        waifuButton.setImageResource(R.drawable.waifu);

        waifuButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        waifuButton.setPadding(0, 0, 0, 0);

        // Set layout parameters for position using RelativeLayout parameters.
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT
                256, 256
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.setMargins(0, 0, 32, 32); // default margin in pixels
        waifuButton.setLayoutParams(params);

        // Set default click behavior.
        waifuButton.setOnClickListener(view -> {
            Toast.makeText(this, "waifu clicked", Toast.LENGTH_SHORT).show();
        });

        // Once layout is complete, adjust the button's position if saved.
        waifuButton.addOnLayoutChangeListener(new ViewGroup.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(android.view.View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                waifuButton.removeOnLayoutChangeListener(this);
                loadButtonPosition();
                if (lastX != -1 && lastY != -1) {
                    waifuButton.setX(lastX);
                    waifuButton.setY(lastY);
                }
            }
        });

        // Listen for drag events to save the new position.
        waifuButton.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;
            float downRawX, downRawY;
            final float CLICK_DRAG_THRESHOLD = 10.0f;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downRawX = motionEvent.getRawX();
                        downRawY = motionEvent.getRawY();
                        dX = view.getX() - downRawX;
                        dY = view.getY() - downRawY;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        view.setX(motionEvent.getRawX() + dX);
                        view.setY(motionEvent.getRawY() + dY);
                        return true;
                    case MotionEvent.ACTION_UP:
                        float upRawX = motionEvent.getRawX();
                        float upRawY = motionEvent.getRawY();
                        if (Math.abs(upRawX - downRawX) < CLICK_DRAG_THRESHOLD &&
                                Math.abs(upRawY - downRawY) < CLICK_DRAG_THRESHOLD) {
                            view.performClick();
                        }
                        // Save the current x and y values
                        lastX = waifuButton.getX();
                        lastY = waifuButton.getY();
                        saveButtonPosition();
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Finally add the button to the root view.
        if (rootView != null) {
            rootView.addView(waifuButton);
        }
    }

    private void saveButtonPosition() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_BUTTON_X, lastX);
        editor.putFloat(KEY_BUTTON_Y, lastY);
        editor.apply();
    }

    private void loadButtonPosition() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        lastX = prefs.getFloat(KEY_BUTTON_X, -1);
        lastY = prefs.getFloat(KEY_BUTTON_Y, -1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the button's position on pause.
        if (waifuButton != null) {
            lastX = waifuButton.getX();
            lastY = waifuButton.getY();
            saveButtonPosition();
        }
        try {
            unregisterReceiver(batteryReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver not registered
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activeActivities++;
        sendAppStateToService("APP_OPENED");
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeActivities--;
        if (activeActivities == 0) {
            sendAppStateToService("APP_CLOSED");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register battery receiver
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(BatteryLevelReceiver.ACTION_BATTERY_LOW);
        batteryFilter.addAction(BatteryLevelReceiver.ACTION_BATTERY_OKAY);
        registerReceiver(batteryReceiver, batteryFilter);

        // Request current battery status
        registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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