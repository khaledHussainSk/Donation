package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.khaled.donation.databinding.ActivityWelcomeLoginBinding;

public class WelcomeLoginActivity extends AppCompatActivity {
    public static final String ONBOARDING_KEY = "ONBOARDING_KEY";
    ActivityWelcomeLoginBinding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editt = sp.edit();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeLoginActivity.this,LoginActivity.class);
                editt.putString(ONBOARDING_KEY,"done");
                editt.apply();
                startActivity(intent);
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeLoginActivity.this,RegistrarActivity.class);
                editt.putString(ONBOARDING_KEY,"done");
                editt.apply();
                startActivity(intent);
            }
        });
    }
}