package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.khaled.donation.databinding.ActivitySignUpCompanyBinding;


public class SignUpCompanyActivity extends AppCompatActivity {
    ActivitySignUpCompanyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}