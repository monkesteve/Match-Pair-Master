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

public class DataBase extends AppCompatActivity {

    // varible dictionary
    private final String TABLE_NAME_TL = "TestsLog";
    private final String[] TL_COLUMN = {"testNo","playerName","testDate", "duration", "moves"};

    private SQLiteDatabase db;
    private String sql;
    private Cursor cursor = null;


    public DataBase(){
        // define db
        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/MatchPair", null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    public void updateTestLog(int testNo, double duration, int moves){
        sql = "UPDATE "+TABLE_NAME_TL+" SET duration ="+duration+", moves ="+moves+" WHERE testNo ="+testNo+";";
        db.execSQL(sql);
    }

    public void insertTestLog(int testNo, String playerName, String testDate,double duration, int moves){
        // insert into TestLog
        sql = "INSERT INTO "+TABLE_NAME_TL+"(testNo, playerName, testDate, duration, moves) VALUES" +
                "("+testNo+",'"+playerName+"','"+testDate+"','"+duration+"',"+moves+");";
        db.execSQL(sql);
    }
    public void insertTestLog(String playerName, String testDate,double duration, int moves){
        // insert into TestLog
        int testNo = getMaximumTestNo()+1;
        sql = "INSERT INTO "+TABLE_NAME_TL+"(testNo, playerName, testDate, duration, moves) VALUES" +
                "("+testNo+",'"+playerName+"','"+testDate+"','"+duration+"',"+moves+");";
        db.execSQL(sql);
    }

    public void insertTestLog(String playerName, int moves){
        int testNo = getMaximumTestNo()+1;
        String[][] record = getTestRecord();
        for(int i = 0; i < record.length; i++){
            if(playerName.equals(record[i][1])){
                return;
            }
        }
        sql = "INSERT INTO "+TABLE_NAME_TL+"(testNo, playerName, moves) VALUES" +
                "("+testNo+",'"+playerName+"',"+moves+");";
        db.execSQL(sql);
    }

    public void createTLTD(){
        // create table TestLog and TestDetails
        sql = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_TL+"(" +
                "testNo int PRIMARY KEY," +
                "playerName text," +
                "testDate date," +
                "duration double," +
                "moves int);";
        db.execSQL(sql);
    }

    public int getMaximumTestNo(){
        sql = "SELECT MAX(testNo) FROM "+TABLE_NAME_TL+";";
        cursor = db.rawQuery(sql, null);
        int maxNumber = 0;
        if ((cursor.moveToFirst()) || !(cursor.getCount() ==0)){
            maxNumber = cursor.getInt(0);
        }
        return maxNumber;
    }

    public String[][] getTestRecord(){
        sql = "SELECT testNo, playerName, testDate, duration, moves FROM "+TABLE_NAME_TL+" ORDER BY moves ASC, duration DESC;";
        cursor = db.rawQuery(sql, null);

        String[][] testRecord = new String[20][5];

        int i = 0;
        while (cursor.moveToNext()){
            String playerName = cursor.getString(cursor.getColumnIndex(TL_COLUMN[1]));
            String date = cursor.getString(cursor.getColumnIndex(TL_COLUMN[2]));
            double duration = cursor.getDouble(cursor.getColumnIndex(TL_COLUMN[3]));
            int moves = cursor.getInt(cursor.getColumnIndex(TL_COLUMN[4]));


            testRecord[i][1] = playerName;
            if(date!=null){
                testRecord[i][2] = date.substring(0,10);
            }
            testRecord[i][3] = (int)duration+"s";
            testRecord[i][4] = moves+"";

            if(i >= 19){
                break;
            }else{
                i++;
            }
        }
        for(int j = 0; j < testRecord.length; j++){
            testRecord[j][0] = ""+(j+1);
        }
        return testRecord;
    }

    public void close(){
        db.close();
    }


    public void dropTable(){

        sql = "DROP TABLE IF EXISTS "+ TABLE_NAME_TL +";";
        db.execSQL(sql);
    }


    public String[] showTL(){
        sql = "SELECT * FROM "+TABLE_NAME_TL+" WHERE Duration IS NOT NULL ORDER BY TestDate DESC LIMIT 20;";
        Cursor cursor = db.rawQuery(sql, null);
        String[] dataStr = new String[20];
        int index = 0;
        while (cursor.moveToNext()){
            String playerName = cursor.getString(cursor.getColumnIndex(TL_COLUMN[1]));
            String testDate = cursor.getString(cursor.getColumnIndex(TL_COLUMN[2])).substring(0,10);
            double duration = cursor.getDouble(cursor.getColumnIndex(TL_COLUMN[3]));
            int moves = cursor.getInt(cursor.getColumnIndex(TL_COLUMN[4]));
            dataStr[index] = String.format("%-12s, %-12s, %-5s, %-2s\n",playerName, testDate, (int)duration+"s", moves);
            index++;
        }
        for(int i = 0; i < dataStr.length; i++){
            if(dataStr[i] == null){
                dataStr[i] = "";
            }
        }
        return dataStr;

    }

}
