package com.example.assignment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class DataBase {

    // varible dictionary
    private final String TABLE_NAME_TL = "TestsLog";
    private final String[] TL_COLUMN = {"testNo","playerName","testDate", "duration", "moves", "difficulties"};

    private SQLiteDatabase db;
    private String sql;
    private Cursor cursor = null;


    public DataBase() {
        // define db
        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/MatchPair", null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    public void updateTestLog(int testNo, double duration, int moves, int diff){
        sql = "UPDATE "+TABLE_NAME_TL+" SET duration ="+duration+", moves ="+moves+", difficulties ="+ diff +" WHERE testNo ="+testNo+";";
        db.execSQL(sql);
    }

    public void insertTestLog(int testNo, String playerName, String testDate,double duration, int moves, int diff){
        // insert into TestLog
        sql = "INSERT INTO "+TABLE_NAME_TL+"(testNo, playerName, testDate, duration, moves, difficulties) VALUES" +
                "("+testNo+",'"+playerName+"','"+testDate+"','"+duration+"',"+moves+","+diff+");";
        db.execSQL(sql);
    }
    public void insertTestLog(String playerName, String testDate,double duration, int moves, int diff){
        // insert into TestLog
        int testNo = getMaximumTestNo()+1;
        sql = "INSERT INTO "+TABLE_NAME_TL+"(testNo, playerName, testDate, duration, moves, difficulties) VALUES" +
                "("+testNo+",'"+playerName+"','"+testDate+"','"+duration+"',"+moves+","+diff+");";
        db.execSQL(sql);
    }

    public void insertTestLog(String playerName, int moves, int diff){
        int testNo = getMaximumTestNo()+1;
        String[][] record = getTestRecord();
        for (int i = 0; i < record.length; i++) {
            if (playerName.equals(record[i][1])) {
                return;
            }
        }
        sql = "INSERT INTO "+TABLE_NAME_TL+"(testNo, playerName, moves, difficulties) VALUES" +
                "("+testNo+",'"+playerName+"',"+moves+","+diff+");";
        db.execSQL(sql);
    }

    public int getMaximumTestNo() {
        sql = "SELECT MAX(testNo) FROM " + TABLE_NAME_TL + ";";
        cursor = db.rawQuery(sql, null);
        int maxNumber = 0;
        if ((cursor.moveToFirst()) || !(cursor.getCount() == 0)) {
            maxNumber = cursor.getInt(0);
        }
        return maxNumber;
    }

    public void createTLTD() {
        // create table TestLog and TestDetails
        sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TL + "(" +
                "testNo int PRIMARY KEY," +
                "playerName text," +
                "testDate date," +
                "duration double," +
                "difficulties int," +
                "moves int);";
        db.execSQL(sql);
    }

    public String[][] getTestRecord(){
        sql = "SELECT testNo, playerName, testDate, duration, moves, difficulties FROM "+TABLE_NAME_TL+" ORDER BY difficulties DESC, moves ASC, duration DESC;";
        cursor = db.rawQuery(sql, null);

        String[][] testRecord = new String[20][6];

        int i = 0;
        while (cursor.moveToNext()) {
            String playerName = cursor.getString(cursor.getColumnIndex(TL_COLUMN[1]));
            String date = cursor.getString(cursor.getColumnIndex(TL_COLUMN[2]));
            double duration = cursor.getDouble(cursor.getColumnIndex(TL_COLUMN[3]));
            int moves = cursor.getInt(cursor.getColumnIndex(TL_COLUMN[4]));
            int diff = cursor.getInt(cursor.getColumnIndex(TL_COLUMN[5]));


            testRecord[i][1] = playerName;
            if (date != null) {
                testRecord[i][2] = date.substring(0, 10);
            }
            testRecord[i][3] = (int)duration+"s";
            testRecord[i][4] = moves+"";
            testRecord[i][5] = diff+"";

            if (i >= 19) {
                break;
            } else {
                i++;
            }
        }
        for (int j = 0; j < testRecord.length; j++) {
            testRecord[j][0] = "" + (j + 1);
        }
        return testRecord;
    }

    public void close() {
        db.close();
    }

    public void dropTable() {
        sql = "DROP TABLE IF EXISTS " + TABLE_NAME_TL + ";";
        db.execSQL(sql);
    }

}
