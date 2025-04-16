package com.example.assignment;

import androidx.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreBoard extends BaseActivity {
    private boolean stateChanged;
    TextView selection;
    RecyclerView recycler_view;
    MyAdapter adapter;
    String[][] dataArray;
    //    DataBase db;
    ArrayList<TestLog> testLogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scoreboard);

        recycler_view = findViewById(R.id.rV);
        selection = findViewById(R.id.selection);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        // Fetch data from Firebase using the correct node name "test_logs"
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test_logs");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                testLogList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    TestLog log = child.getValue(TestLog.class);
                    if (log != null) {
                        testLogList.add(log);
                    }
                }
                // Sort test logs
                Collections.sort(testLogList, new Comparator<TestLog>() {
                    @Override
                    public int compare(TestLog a, TestLog b) {
                        if (a.testDate == null && b.testDate == null) return 0;
                        if (a.testDate == null) return 1;
                        if (b.testDate == null) return -1;
                        // Compare b to a to have the newest first
                        return b.testDate.compareTo(a.testDate);
                    }
                });

                // Create a two-dimensional String array
                int size = Math.max(testLogList.size(), 20);
                dataArray = new String[size][6];
                for (int i = 0; i < size; i++) {
                    dataArray[i][0] = String.valueOf(i + 1); // rank
                    if (i < testLogList.size()) {
                        TestLog log = testLogList.get(i);
                        dataArray[i][1] = log.playerName;
                        dataArray[i][2] = log.testDate != null && log.testDate.length() >= 10 ? log.testDate.substring(0, 10) : "";
                        dataArray[i][3] = ((int) log.duration) + "s";
                        dataArray[i][4] = String.valueOf(log.moves);
                        dataArray[i][5] = String.valueOf(log.difficulties);
                    } else {
                        // Fill extra rows with empty strings
                        dataArray[i][1] = "";
                        dataArray[i][2] = "";
                        dataArray[i][3] = "";
                        dataArray[i][4] = "";
                        dataArray[i][5] = "";
                    }
                }

                // Update podium TextViews (first three entries)
                TextView[] podium = new TextView[3];
                podium[0] = findViewById(R.id.first);
                podium[1] = findViewById(R.id.second);
                podium[2] = findViewById(R.id.third);

                for (int i = 0; i < podium.length; i++) {
                    if (i < dataArray.length) {
                        podium[i].setText(dataArray[i][1]); // player name
                    } else {
                        podium[i].setText(""); // clear if no data
                    }
                }

                // Set RecyclerView adapter with the dataArray
                adapter = new MyAdapter(ScoreBoard.this, dataArray);
                recycler_view.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed.
            }
        });
    }

//    public void initiate(){
//        db = new DataBase();
//        dataArray = db.getTestRecord();
//
//        recycler_view = findViewById(R.id.rV);
//        selection = findViewById(R.id.selection);
//        TextView[] podium = new TextView[3];
//        podium[0] = (TextView) findViewById(R.id.first);
//        podium[1] = (TextView) findViewById(R.id.second);
//        podium[2] = (TextView) findViewById(R.id.third);
//
//        for (int i = 0; i < podium.length; i++){
//            podium[i].setText(dataArray[i][1]);
//        }
//
//        adapter = new MyAdapter(this, dataArray);
//        recycler_view.setHasFixedSize(true);
//        recycler_view.setAdapter(adapter);
//        recycler_view.setLayoutManager(new LinearLayoutManager(this));
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 3434) {
            finish();
        }
    }

}
