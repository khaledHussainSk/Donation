package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.khaled.donation.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {
//    ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
//            binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        }catch (Exception e){

        }
        setContentView(R.layout.activity_splash_screen);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}