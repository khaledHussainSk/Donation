package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.khaled.donation.databinding.ActivitySplachScreen2Binding;

public class SplachScreen2 extends AppCompatActivity {

    ActivitySplachScreen2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplachScreen2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SplachScreen3.class);
                startActivity(intent);
            }
        });
    }
}