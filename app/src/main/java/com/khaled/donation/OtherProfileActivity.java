package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.khaled.donation.databinding.ActivityOtherProfileBinding;

public class OtherProfileActivity extends AppCompatActivity {
    ActivityOtherProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}