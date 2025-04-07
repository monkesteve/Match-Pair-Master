package com.example.assignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    Button start;
    Intent foregroundServiceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        DataBase db = new DataBase();
        db.createTLTD();

        foregroundServiceIntent = new Intent(this, ForegroundService.class);
        startForegroundService();

        sendAppStateToService("APP_OPENED");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // App is visible again
        sendAppStateToService("APP_OPENED");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // This will be called when app goes to background
        sendAppStateToService("APP_CLOSED");
    }

    private void startForegroundService() {
        // Use startForegroundService for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foregroundServiceIntent);
        } else {
            startService(foregroundServiceIntent);
        }
    }

    public void start(final View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imagebtn));
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Player Name");
        alertDialog.setMessage("Enter player name");

        final EditText input = new EditText(MainActivity.this);
        input.setSingleLine(true);

        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_baseline_face_24);

        // Button for Level 1 (set as positive)
        alertDialog.setPositiveButton("Level 1", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String playerName = input.getText().toString();
                if (!playerName.equals("") && playerName.length() > 2 && playerName.length() < 12) {
                    Intent intent = new Intent(MainActivity.this, Create.class);
                    intent.putExtra("playerName", playerName);
                    intent.putExtra("difficulties", 1);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid player name! \n" +
                            "Please enter text within 3 to 12 characters!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Button for Level 2 (set as negative)
        alertDialog.setNegativeButton("Level 2", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String playerName = input.getText().toString();
                if (!playerName.equals("") && playerName.length() > 2 && playerName.length() < 12) {
                    Intent intent = new Intent(MainActivity.this, Create.class);
                    intent.putExtra("playerName", playerName);
                    intent.putExtra("difficulties", 2);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid player name! \n" +
                            "Please enter text within 3 to 12 characters!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Button for Level 3 (set as neutral)
        alertDialog.setNeutralButton("Level 3", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String playerName = input.getText().toString();
                if (!playerName.equals("") && playerName.length() > 2 && playerName.length() < 12) {
                    Intent intent = new Intent(MainActivity.this, Create.class);
                    intent.putExtra("playerName", playerName);
                    intent.putExtra("difficulties", 3);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid player name! \n" +
                            "Please enter text within 3 to 12 characters!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    public void scoreBoard(View v){
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imagebtn));
        Intent myIntent = new Intent(MainActivity.this, ScoreBoard.class);
        startActivity(myIntent);
    }

    public void textlog(View v){
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imagebtn));
        Toast.makeText(MainActivity.this, "To be implemented", Toast.LENGTH_LONG).show();
    }

    public void testNotif(View v){
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imagebtn));
        startForegroundService();
    }
}