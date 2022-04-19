package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.khaled.donation.databinding.ActivityOnBoarding02Binding;

public class OnBoardingActivity02 extends AppCompatActivity {
    ActivityOnBoarding02Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoarding02Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnBoardingActivity02.this,WelcomeLoginActivity.class);
                startActivity(intent);
            }
        });

    }
}