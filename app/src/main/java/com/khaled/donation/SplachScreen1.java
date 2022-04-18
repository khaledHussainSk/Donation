package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.khaled.donation.databinding.ActivitySplachScreen1Binding;

public class SplachScreen1 extends AppCompatActivity {

    ActivitySplachScreen1Binding binding;
    boolean b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplachScreen1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        b = preferences.getBoolean("b",false);

        if (b){
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }

        binding.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SplachScreen2.class);
                startActivity(intent);
            }
        });
    }
}