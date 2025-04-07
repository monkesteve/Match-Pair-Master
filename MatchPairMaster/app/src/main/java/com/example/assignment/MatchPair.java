package com.example.assignment;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

public class MatchPair extends BaseActivity {
    LocalTime questionStart;
    LocalDate startDate;
    String playerName;
    int diff;
    int testNo;
    int questionCount = 4;
    final int[] animals = {R.drawable.rabbit, R.drawable.elephant, R.drawable.lion, R.drawable.cat, R.drawable.bee, R.drawable.shark, R.drawable.penguin, R.drawable.otter};
    int correctCount;
    int btnIndex;

    int moves;
    int yourAnswer, tmpAnswer;
    TextView tvPlayer, tvMoves;
    ImageButton A1,A2,A3,A4,A5,A6,A7,A8,A9,A10,A11,A12,A13,A14,A15,A16;
    boolean memStart = false;
    int[] buttonAns = new int[8];
    ImageButton[] btns;
    Intent local;

    MediaPlayer mp, mp2;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_matchpair);

        questionStart = LocalTime.now();
        startDate = LocalDate.now();
        local = getIntent();
        playerName = local.getStringExtra("playerName");
        btnIndex = -1;
        diff = local.getIntExtra("difficulties", 1);

        // View initialize
        tvPlayer = findViewById(R.id.tvPlayer);
        tvMoves = findViewById(R.id.tvMoves);
        tvMoves.setText("Your Moves: " + moves);
        A1 = (ImageButton) findViewById(R.id.A1);
        A2 = (ImageButton)findViewById(R.id.A2);
        A3 = (ImageButton)findViewById(R.id.A3);
        A4 = (ImageButton)findViewById(R.id.A4);
        A5 = (ImageButton)findViewById(R.id.A5);
        A6 = (ImageButton)findViewById(R.id.A6);
        A7 = (ImageButton)findViewById(R.id.A7);
        A8 = (ImageButton)findViewById(R.id.A8);
        A9 = (ImageButton) findViewById(R.id.A9);
        A10 = (ImageButton)findViewById(R.id.A10);
        A11 = (ImageButton)findViewById(R.id.A11);
        A12 = (ImageButton)findViewById(R.id.A12);
        A13 = (ImageButton)findViewById(R.id.A13);
        A14 = (ImageButton)findViewById(R.id.A14);
        A15 = (ImageButton)findViewById(R.id.A15);
        A16 = (ImageButton)findViewById(R.id.A16);
        btns = new ImageButton[]{A1,A2,A3,A4,A5,A6,A7,A8,A9,A10,A11,A12,A13,A14,A15,A16};
        mp = MediaPlayer.create(getApplicationContext(), R.raw.filpsound3);
        mp2 = MediaPlayer.create(getApplicationContext(), R.raw.success);
        tvPlayer.setText("Fighting ! " + playerName);

        switch (diff){
            case 1:
                for (int i = 8; i < btns.length; i++) {
                    btns[i].setVisibility(View.GONE);
                }
                break;
            case 2:
                questionCount = 6;
                for (int i = 12; i < btns.length; i++) {
                    btns[i].setVisibility(View.GONE);
                }
                break;
            case 3:
                questionCount = 8;
                break;
        }


        // main process
        // random assign number on buttons
        buttonAns = genRandom();

    }

    // onclick method
    public void ansClick(View view) {
        mp.start();

        for (int i = 0; i < buttonAns.length; i++) {
            if (view.getId() == btns[i].getId()) {
                if (btnIndex == i) {
                    return;
                }
                btnIndex = i;
                yourAnswer = buttonAns[i];
                break;
            }
        }

        BtnClearTask btnTask = new BtnClearTask();
        btnTask.setOnFinishShowDigitListener(new BtnClearTask.onFinishShowDigitListener() {

            @Override
            public void onFinishShowDigit() {
                for (int i = 0; i < buttonAns.length; i++) {
                    if (btns[i].getTag(R.id.tag_status) == "done") {
                        mp2.start();
                        btns[i].setBackground(null);
                        btns[i].setEnabled(false);
                        btns[i].setTag(R.id.tag_status, "");
                    }

                }
                for (int i = 0; i < btns.length; i++) {
                    if (btns[i].getTag(R.id.tag_status) == "showing") {
                        mp.start();
                        btns[i].setTag(R.id.tag_status, "");
                        btns[i].animate().rotationYBy(-180);

                    }
                    btns[i].setImageResource(android.R.color.transparent);
                    if (btns[i].getBackground() != null) {
                        btns[i].setEnabled(true);
                    }
                }
            }


        });

        if (!memStart) {
            tmpAnswer = yourAnswer;
            view.setTag(R.id.tag_status, "showing");
            ImageButton card = (ImageButton) view;
            card.setImageResource(0);
            card.animate().rotationYBy(180).withEndAction(new Runnable() {
                @Override
                public void run() {
                    btns[btnIndex].setImageResource(animals[yourAnswer - 1]);
                }
            });
        } else {
            for (int i = 0; i < btns.length; i++) {
                btns[i].setEnabled(false);
            }


            if (yourAnswer == tmpAnswer) {
                for (int i = 0; i < buttonAns.length; i++) {
                    if (buttonAns[i] == yourAnswer) {
                        btns[i].setTag(R.id.tag_status, "done");
                    }
                }
                correctCount++;
            } else {
                view.setTag(R.id.tag_status, "showing");
            }
            ImageButton card = (ImageButton) view;
            card.setImageResource(0);
            card.animate().rotationYBy(180).withEndAction(new Runnable() {
                @Override
                public void run() {
                    btns[btnIndex].setImageResource(animals[yourAnswer - 1]);
                    btnTask.execute();
                    tmpAnswer = 0;
                    yourAnswer = 0;
                    btnIndex = -1;
                    moves++;
                    if (correctCount >= questionCount) {
                        finishGame();
                    }
                }
            });

        }


        tvMoves.setText("Your Moves: " + moves);
        memStart = !memStart;
    }


    public void finishGame() {
        // end
        mp.release();
        double duration = questionStart.until(LocalTime.now(), ChronoUnit.SECONDS);
        Intent intent = new Intent(MatchPair.this, Finish.class);
        intent.putExtra("testNo", this.testNo);
        intent.putExtra("playerName", this.playerName);
        intent.putExtra("result", this.moves);
        intent.putExtra("duration", duration);
        intent.putExtra("date", convertToDateViaInstant(startDate).toString());
        intent.putExtra("difficulties", this.diff);
        startActivity(intent);
        finish();
    }


    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public int[] genRandom() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= questionCount; i++) {
            list.add(i);
            list.add(i);
        }
        Collections.shuffle(list);
        Object[] arr = list.toArray();
        int[] intarr = new int[questionCount*2];
        for(int i = 0; i < questionCount*2; i++){
            intarr[i] = (int) arr[i];
        }
        return intarr;
    }

}
