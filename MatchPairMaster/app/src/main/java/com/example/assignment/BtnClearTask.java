package com.example.assignment;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class BtnClearTask extends AsyncTask<Integer, Integer, Integer> {
    public interface onFinishShowDigitListener {
        public void onFinishShowDigit();
    }
    private onFinishShowDigitListener listener;
    public BtnClearTask(){
    }


    public void setOnFinishShowDigitListener(onFinishShowDigitListener listener){
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Integer... integers){
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer i){
        super.onPostExecute(i);
        listener.onFinishShowDigit();
    }

}
