package com.example.assignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button start;
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
    }

    public void start(final View view){
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imagebtn));
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Player Name");
        alertDialog.setMessage("Enter player name");

        final EditText input = new EditText(MainActivity.this);
        input.setSingleLine(true);

        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_baseline_face_24);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String playerName = input.getText().toString();
                        if (!playerName.equals("") && playerName.length() > 2 && playerName.length() < 12) {
                            Intent myIntent1 = new Intent(MainActivity.this, Create.class);
                            myIntent1.putExtra("playerName",playerName);
                            startActivity(myIntent1);
                        }else {
                            Toast.makeText(MainActivity.this, "Invalid player name! \n" +
                                    "Please enter text within 3 to 12 character!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
}