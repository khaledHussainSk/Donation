package com.khaled.donation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.khaled.donation.databinding.ActivityDisplayImageBinding;

public class DisplayImageActivity extends AppCompatActivity {
    ActivityDisplayImageBinding binding;
    String imageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();

    }

    private void fixed() {
        Intent intent = getIntent();
        imageString = intent.getStringExtra(AddPhotoActivity.IMAGE_KEY);
        Glide.with(getBaseContext()).load(imageString).into(binding.iv);
        binding.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}