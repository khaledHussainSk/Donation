package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.khaled.donation.databinding.ActivitySplachScreen3Binding;

public class SplachScreen3 extends AppCompatActivity {

    ActivitySplachScreen3Binding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    boolean b = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplachScreen3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        b = preferences.getBoolean("b",false);

        if (b){
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }


        binding.btnGo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = sp.edit();
                editor.putBoolean("b" ,true );
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),SplachScreen4.class);
                startActivity(intent);
                finish();
            }
        });
    }
}