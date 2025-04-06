package com.example.assignment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.view.View;

public class ScoreBoard extends AppCompatActivity{
    private boolean stateChanged;
    TextView selection;
    RecyclerView recycler_view;
    MyAdapter adapter;
    String[][] dataArray;
    DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_scoreboard);

        initiate();
    }

    public void initiate(){
        db = new DataBase();
        dataArray = db.getTestRecord();

        recycler_view = findViewById(R.id.rV);
        selection = findViewById(R.id.selection);
        TextView[] podium = new TextView[3];
        podium[0] = (TextView) findViewById(R.id.first);
        podium[1] = (TextView) findViewById(R.id.second);
        podium[2] = (TextView) findViewById(R.id.third);

        for (int i = 0; i < podium.length; i++){
            podium[i].setText(dataArray[i][1]);
        }

        adapter = new MyAdapter(this, dataArray);
        recycler_view.setHasFixedSize(true);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 3434){
            finish();
        }
    }

}
