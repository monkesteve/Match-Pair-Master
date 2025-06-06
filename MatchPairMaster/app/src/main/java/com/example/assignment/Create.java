package com.example.assignment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class Create extends BaseActivity {
    String result;
    String playerName;
    int diff;

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
        setContentView(R.layout.splash);
        Intent intent = getIntent();
        diff = intent.getIntExtra("difficulties", 1);
        playerName = intent.getStringExtra("playerName");

        Intent myIntent = new Intent(this, MatchPair.class);
        myIntent.putExtra("playerName", this.playerName);
        myIntent.putExtra("difficulties", this.diff);
        startActivity(myIntent);
        finish();
    }
}
