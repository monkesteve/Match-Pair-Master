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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.splash);
        Intent intent = getIntent();
        playerName = intent.getStringExtra("playerName");

        Intent myIntent = new Intent(this, MatchPair.class);
        myIntent.putExtra("playerName", this.playerName);
        startActivity(myIntent);
        finish();
    }
}
