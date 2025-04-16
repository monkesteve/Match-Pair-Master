package com.example.assignment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Finish extends BaseActivity {

    TextView tvCong, tvResult;
    String playerName;
    int result;
    String date;
    int diff;
    double duration;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Request the feature before calling super.onCreate()
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        // Hide the ActionBar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_finish);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.win);
        mp.start();
        Intent intent = getIntent();
        playerName = intent.getStringExtra("playerName");
        result = intent.getIntExtra("result", 0);
        duration = intent.getDoubleExtra("duration", 0);
        date = intent.getStringExtra("date");
        diff = intent.getIntExtra("difficulties", 1);
        DataBase db = new DataBase();
        db.insertTestLog(playerName, date, duration, result, diff);
        db.close();

        // Use Firebase to insert the same test log record
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        TestLog testLog = new TestLog();
        // Assuming you want to use result as the test number or get a new unique test number
        testLog.testNo = result;
        testLog.playerName = playerName;
        testLog.testDate = date;
        testLog.duration = duration;
        testLog.moves = result;
        testLog.difficulties = diff;
        firebaseHelper.insertTestLog(testLog);


        tvCong = findViewById(R.id.tvCong);
        tvResult = findViewById(R.id.tvResult);

        tvCong.setText(playerName);
        tvResult.setText("Your Moves: " + result +"\n\n" +
                "Time spent: "+(int)duration+"s\n\n"+
                "Level: "+(int)diff+"\n\n");

    }

    public void goBack(View v){
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imagebtn));
        Intent intent = new Intent(Finish.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goScoreboard(View v){
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imagebtn));
        Intent intent = new Intent(Finish.this, ScoreBoard.class);
        startActivity(intent);
//        finish();
    }

    public void goShare(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imagebtn));

        // Prepare content values for the provider
        ContentValues values = new ContentValues();
        values.put("playerName", playerName);
        values.put("moves", result);
        values.put("duration", duration);
        values.put("testDate", date);

        // Insert data into the content provider
        Uri resultUri = getContentResolver().insert(GameResultProvider.CONTENT_URI, values);

        // Query the content provider to get the data back (demonstrates the full content provider workflow)
        Cursor cursor = getContentResolver().query(resultUri, null, null, null, null);

        // Create share text using data from content provider
        String shareBody;
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("playerName"));
            int moves = cursor.getInt(cursor.getColumnIndex("moves"));
            double time = cursor.getDouble(cursor.getColumnIndex("duration"));
            shareBody = name + " has completed the Match Pair game Level "+diff+" in " + (int)time + " seconds with " + moves + " moves!";
            cursor.close();
        } else {
            // Fallback if cursor fails
            shareBody = "I have completed the Match Pair game Level "+diff+" in " + (int)duration + " seconds with " + result + " moves!";
        }

        // Create the share intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Match Pair Game");
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);

        // Start the share activity
        startActivity(Intent.createChooser(intent, "Share using"));
    }
}
