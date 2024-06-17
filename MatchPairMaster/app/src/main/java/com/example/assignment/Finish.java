package com.example.assignment;

import android.content.Intent;
import android.media.MediaPlayer;
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

public class Finish extends AppCompatActivity {

    TextView tvCong, tvResult;
    String playerName;
    int result;
    String date;
    double duration;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
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
        DataBase db = new DataBase();
        db.insertTestLog(playerName, date, duration, result);
        db.close();


        tvCong = findViewById(R.id.tvCong);
        tvResult = findViewById(R.id.tvResult);

        tvCong.setText(playerName);
        tvResult.setText("Your Moves: " + result +"\n\n" +
                "Time spent: "+(int)duration+"s\n\n");

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
        finish();
    }
}
