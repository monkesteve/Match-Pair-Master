package com.example.assignment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public class MatchPair extends BaseActivity implements MatchPairGridFragment.MatchPairGridListener {

    private LocalTime questionStart;
    private LocalDate startDate;
    private String playerName;
    private int diff;
    private int testNo;
    private int moves;
    private TextView tvPlayer, tvMoves;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_matchpair);

        questionStart = LocalTime.now();
        startDate = LocalDate.now();
        Intent local = getIntent();
        playerName = local.getStringExtra("playerName");
        diff = local.getIntExtra("difficulties", 1);

        tvPlayer = findViewById(R.id.tvPlayer);
        tvMoves = findViewById(R.id.tvMoves);
        tvPlayer.setText("Fighting ! " + playerName);
        tvMoves.setText("Your Moves: 0");

        // Add Fragment
        MatchPairGridFragment fragment = new MatchPairGridFragment();
        Bundle args = new Bundle();
        args.putInt("difficulty", diff);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onMoveUpdated(int moves) {
        tvMoves.setText("Your Moves: " + moves);
        this.moves = moves;
    }

    @Override
    public void onGameFinished() {
        finishGame();
    }

    private void finishGame() {
        double duration = questionStart.until(LocalTime.now(), ChronoUnit.SECONDS);
        LocalDateTime timestamp = LocalDateTime.now();
        Date timestampDate = Date.from(timestamp.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        String formattedDate = sdf.format(timestampDate);

        Intent intent = new Intent(this, Finish.class);
        intent.putExtra("testNo", testNo);
        intent.putExtra("playerName", playerName);
        intent.putExtra("result", moves);
        intent.putExtra("duration", duration);
        intent.putExtra("date", formattedDate);
        intent.putExtra("difficulties", diff);
        startActivity(intent);
        finish();
    }
}