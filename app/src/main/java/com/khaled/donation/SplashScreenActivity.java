package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.khaled.donation.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String isDone = sp.getString(WelcomeLoginActivity.ONBOARDING_KEY,null);
        String isChecked = sp.getString(LoginActivity.ISCHECKED_KEY,null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    if (isDone == null){
                        Intent intent = new Intent(getBaseContext(),WelcomeScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        if (isChecked == null){
                            Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Intent intent = new Intent(getBaseContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}