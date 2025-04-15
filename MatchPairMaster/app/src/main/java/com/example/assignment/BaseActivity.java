package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

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

    private int[] audioResources = {R.raw.do_not_touch_me, R.raw.scoreboard};

    private Button summonWaifuButton;
    private boolean isWaifuButtonVisible = false;

    private View speechBubble;
    private Map<Integer, String> audioTextMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        summonWaifuButton= findViewById(R.id.showWaifuButton);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
//        addWaifuButton();

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

    public void summonWaifu(View view) {
        if (isWaifuButtonVisible == false) {
            addWaifuButton();
            isWaifuButtonVisible = true;
        } else {
            waifuButton.setVisibility(View.VISIBLE);
        }
    }

    private void addWaifuButton() {
        if (isWaifuButtonVisible) {
            return; // if button already existed, don't create again
        }
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

        audioTextMap = new HashMap<>();
        audioTextMap.put(R.raw.greetings, "Well it's been a while, click the button below to start the game!");
        audioTextMap.put(R.raw.do_not_touch_me, "Don't touch me! Play the game");
        audioTextMap.put(R.raw.scoreboard,"If you click If you click score board, you can see the ranking of every player");



        // Set default click behavior.
        waifuButton.setOnClickListener(view -> {
            int randomIndex = (int) (Math.random() * audioResources.length);
            int selectedAudio = audioResources[randomIndex];
            playAudioWithSpeechBubble(selectedAudio);
//            Toast.makeText(this, "waifu clicked", Toast.LENGTH_SHORT).show();
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

        //release after playing
        playAudioWithSpeechBubble(R.raw.greetings);


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

        speechBubble = new TextView(this);
        ((TextView) speechBubble).setText("");
        speechBubble.setBackgroundResource(R.drawable.speech_bubble_background);
        speechBubble.setPadding(16, 16, 16, 16);
        speechBubble.setVisibility(View.GONE); // 默认隐藏

        RelativeLayout.LayoutParams bubbleParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        rootView.addView(speechBubble, bubbleParams);
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

    private void playAudioWithSpeechBubble(int audioResId) {
        // 播放音频
        MediaPlayer mediaPlayer = MediaPlayer.create(this, audioResId);
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            speechBubble.setVisibility(View.GONE); // 音频播放完成后隐藏对话气泡
        });
        mediaPlayer.start();

        // 更新对话气泡文本
        String text = audioTextMap.get(audioResId);
        if (text != null) {
            updateSpeechBubble(text);
        }
    }

    private void updateSpeechBubble(String text) {
        if (speechBubble != null) {
            ((TextView) speechBubble).setText(text);
            speechBubble.setVisibility(View.VISIBLE);
            updateSpeechBubblePosition();
        }
    }

    private void updateSpeechBubblePosition() {
        if (waifuButton != null && speechBubble != null) {
            // 获取按钮的位置
            int[] buttonLocation = new int[2];
            waifuButton.getLocationOnScreen(buttonLocation);

            // 设置对话气泡的位置
            speechBubble.setX(buttonLocation[0] + waifuButton.getWidth() + 16); // 按钮右侧偏移
            speechBubble.setY(buttonLocation[1]); // 与按钮垂直对齐
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

        // Register for your custom battery actions
        IntentFilter customBatteryFilter = new IntentFilter();
        customBatteryFilter.addAction(BatteryLevelReceiver.ACTION_BATTERY_LOW);
        customBatteryFilter.addAction(BatteryLevelReceiver.ACTION_BATTERY_OKAY);
        registerReceiver(batteryReceiver, customBatteryFilter);

        // Register for system battery changes and get current battery level
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(new BatteryLevelReceiver(), batteryFilter);
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