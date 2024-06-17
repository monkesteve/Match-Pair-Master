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

public class Create extends AppCompatActivity implements OnDownloadFinishListener {
    DataBase db;
    String result;
    String playerName;
    MyAsyncTask task = null;
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
        db = new DataBase();

        getData();
    }

    public void getData(){
        if(task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            task = new MyAsyncTask();
            task.setOnDownloadFinishListener(this);
            task.execute();
        }
    }

    @Override
    public void updateDownloadResult(String result) {
        // do in background by Asynctask
        this.result = result;
        try{
            JSONArray arr = new JSONArray(result);
            String[] players = new String[5];
            int[] Moves = new int[5];
            for(int i = 0; i < 5; i++){
                players[i] = arr.getJSONObject(i).getString("Name");
                Moves[i] = arr.getJSONObject(i).getInt("Moves");
            }

            // initial Data Base
            for(int i = 0; i < players.length; i++){
                db.insertTestLog(players[i], Moves[i]);
            }
            Intent myIntent = new Intent(this, MatchPair.class);
            myIntent.putExtra("playerName", this.playerName);
            startActivity(myIntent);
            finish();
        }catch (Exception e){
            result = e.getMessage();
        }
    }
}
