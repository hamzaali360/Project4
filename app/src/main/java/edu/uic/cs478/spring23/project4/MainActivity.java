package edu.uic.cs478.spring23.project4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Handle the message from the worker threads here
            }
        };
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update the UI here
            }
        });
    }
}